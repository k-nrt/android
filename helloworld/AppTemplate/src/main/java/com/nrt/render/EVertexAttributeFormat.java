package com.nrt.render;

/**
 * Created by itari on 2017/05/13.
 */

import android.opengl.GLES20;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EVertexAttributeFormat
{
    Unknown(0),
    Float(GLES20.GL_FLOAT),
    Integer(GLES20.GL_INT),
    Short(GLES20.GL_SHORT),
    Byte(GLES20.GL_BYTE),
    UnsignedInteger(GLES20.GL_UNSIGNED_INT),
    UnsignedShort(GLES20.GL_UNSIGNED_SHORT),
    UnsignedByte(GLES20.GL_UNSIGNED_BYTE);

    public int Value = 0;
    EVertexAttributeFormat(int value)
    {
        Value = value;
    }

    public static HashMap<Integer,EVertexAttributeFormat> s_mapValues = CreateValueMap();
    public static HashMap<Integer,EVertexAttributeFormat> CreateValueMap()
    {
        HashMap<Integer,EVertexAttributeFormat> hashMap = new HashMap<java.lang.Integer, EVertexAttributeFormat>();

        for( EVertexAttributeFormat format : EVertexAttributeFormat.values())
        {
            hashMap.put(format.Value,format);
        }

        return hashMap;
    }

    public static EVertexAttributeFormat Find(int value)
    {
        if( s_mapValues.containsKey(value) )
        {
            return s_mapValues.get(value);
        }

        return Unknown;
    }
}
