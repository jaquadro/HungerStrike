package com.jaquadro.minecraft.hungerstrike.command;

import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.ResourceLocation;

public class ArgumentHandler
{
    public static void init() {
        ArgumentTypes.register(new ResourceLocation("hungerstrike.operation"), OperationArgument.class, new ArgumentSerializer(OperationArgument::operation));
    }
}
