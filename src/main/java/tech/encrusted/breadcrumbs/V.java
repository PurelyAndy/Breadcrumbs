package tech.encrusted.breadcrumbs;

public class V {
    public static
    //? if <=1.20.6 {
    /*void*/
    //?} else {
    net.minecraft.client.render.BufferBuilder
    //?}
    begin(
    //? if <=1.20.6 {
    /*net.minecraft.client.render.BufferBuilder*/
    //?} else {
    net.minecraft.client.render.Tessellator
    //?}
    obj,
    //? if <=1.16.5 {
    /*int*/
    //?} else {
    com.mojang.blaze3d.vertex.VertexFormat.DrawMode
    //?}
    mode,
    com.mojang.blaze3d.vertex.VertexFormat format
    ) {
        //? if >=1.21 {
        return
        //?}
        obj.begin(mode, format);
    }
}
