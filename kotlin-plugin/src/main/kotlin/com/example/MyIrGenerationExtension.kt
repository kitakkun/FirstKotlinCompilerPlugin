package com.example

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
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

        val currentTimeMillisFunction = pluginContext.referenceFunctions(FqName("kotlin.system.currentTimeMillis"))
            .single { it.owner.valueParameters.isEmpty() }

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

        body.statements.add(0, startTimeVariable)

        return transformed
    }
}
