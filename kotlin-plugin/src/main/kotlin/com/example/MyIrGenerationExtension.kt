package com.example

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class MyIrGenerationExtension(
    private val annotations: List<String>,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(
            MyIrTransformer(
                annotations = annotations,
                pluginContext = pluginContext,
            )
        )
    }
}

class MyIrTransformer(
    private val annotations: List<String>,
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {
    @OptIn(FirIncompatiblePluginAPI::class)
    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val transformed = super.visitFunctionNew(declaration)
        val hasAnnotation = annotations.any { declaration.hasAnnotation(FqName(it)) }
        if (!hasAnnotation) return transformed
        val body = declaration.body as? IrBlockBody ?: return transformed

        val systemClass = pluginContext.referenceClass(FqName("java.lang.System"))
        val currentTimeMillisFunction = systemClass?.owner?.declarations?.filterIsInstance<IrSimpleFunction>()
            ?.singleOrNull { it.name == Name.identifier("currentTimeMillis") && it.valueParameters.isEmpty() }?.symbol
            ?: error("currentTimeMillis not found")

        val printlnFunction = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
            .firstOrNull {
                it.owner.name == Name.identifier("println") &&
                        it.owner.valueParameters.size == 1 &&
                        it.owner.valueParameters.single().type.isNullableAny()
            } ?: error("println function not found")

        val currentTimeMillisCall = IrCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = pluginContext.irBuiltIns.longType,
            symbol = currentTimeMillisFunction,
            typeArgumentsCount = 0,
            valueArgumentsCount = 0,
        )

        val startTimeVariable = IrVariableImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            symbol = IrVariableSymbolImpl(),
            name = Name.identifier("startTime"),
            isVar = false,
            isConst = false,
            isLateinit = false,
            type = pluginContext.irBuiltIns.longType,
        )

        startTimeVariable.initializer = currentTimeMillisCall

        val printlnCall = IrCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = pluginContext.irBuiltIns.unitType,
            symbol = printlnFunction,
            typeArgumentsCount = 0,
            valueArgumentsCount = 1,
        )

        printlnCall.putValueArgument(
            index = 0,
            valueArgument = IrGetValueImpl(
                startOffset = declaration.startOffset,
                endOffset = declaration.endOffset,
                symbol = startTimeVariable.symbol
            )
        )

        startTimeVariable.parent = declaration

        body.statements.add(0, startTimeVariable)
        body.statements.add(1, printlnCall)

        return declaration
    }
}
