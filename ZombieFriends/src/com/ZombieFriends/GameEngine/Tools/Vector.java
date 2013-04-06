package com.ZombieFriends.GameEngine.Tools;

public class Vector
{
	private float x;
	private float y;
	
	public Vector(float x, float y)
	{
		super();
		this.setX(x);
		this.setY(y);
	}
	
	public Vector copy()	//create a new vector that is a replica of the current vector
	{
		return new Vector(x,y);
	}
	
	public float getX()
	{
		return x;
	}
	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public static Vector add(Vector a, Vector b)	//create a new vector that is the sum of two vectors
	{
		Vector sum = new Vector(0,0);
		sum.x = a.x + b.x;
		sum.y = a.y + b.y;
		return sum;
		
	}
	
	public static Vector sub(Vector a, Vector b)	//create a new vector that is the result of subtracting two vectors
	{
		Vector sub = new Vector(0,0);
		sub.x = a.x - b.x;
		sub.y = a.y - b.y;
		return sub;
		
	}
	
	public static Vector multiply(Vector a, float b)	//create a new vector that is the result of multiplying two vectors
	{
		Vector product = new Vector(0,0);
		product.x = a.x * b;
		product.y = a.y * b;
		return product;
	}
	
	public static Vector divide(Vector a, float b)	//create a new vector that is the result of dividing two vectors
	{
		Vector product = new Vector(0,0);
		product.x = a.x / b;
		product.y = a.y / b;
		return product;
	}

}
