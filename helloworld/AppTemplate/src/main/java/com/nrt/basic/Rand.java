package com.nrt.basic;


import java.util.Random;

public class Rand
{
	private Random m_random = new Random();

	public float Float()
	{
		return m_random.nextFloat();
	}

	public float Float(float fMin, float fMax)
	{
		return fMin + (fMax - fMin) * m_random.nextFloat();
	}

	public int Integer(int nMin, int nMax)
	{
		return nMin + (m_random.nextInt() % (nMax - nMin + 1));
	}
	
	public int Integer()
	{
		return m_random.nextInt();
	}
}
