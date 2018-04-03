import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

final public class GLBezier {

	public static void drawShape(GL2 gl){

		float[] mesh1 = getFlatMesh();
		
		//First row of points
		addPoint(mesh1, 0, 1, -2, 2, -1);
		addPoint(mesh1, 1, 1, 0, 2, -1);
		addPoint(mesh1, 2, 1, 0, 2, -1);
		addPoint(mesh1, 3, 1, 2, 2, -1);
		
		//Second row of points
		addPoint(mesh1, 0, 3, 0, 5f, 2);
		addPoint(mesh1, 1, 3, 0, 5f, 2);
		addPoint(mesh1, 2, 3, 0, 5f, 2);
		addPoint(mesh1, 3, 3, 0, 5f, 2);

		//Third row of points
		addPoint(mesh1, 0, 3, 0, 10, 0);
		addPoint(mesh1, 1, 3, 0, 10, 0);
		addPoint(mesh1, 2, 3, 0, 10, 0);
		addPoint(mesh1, 3, 3, 0, 10, 0);
		
		gl.glColor3f(1, 0, 0);
		drawBezierPatch(gl, mesh1, true);
		gl.glColor3f(1, 1, 1);
		drawControls(gl, mesh1);
	}
	
	private static void drawControls(GL2 gl, float[] ctrlpoints) {
		int uorder = 4, vorder = 4;
	    int u, v;
	    for(u = 0; u < uorder; u++) {
	        gl.glBegin(GL.GL_LINE_STRIP);
	        for(v = 0; v < 4; v++){
	            gl.glVertex3f(ctrlpoints[v*12+u*3+0],
	                          ctrlpoints[v*12+u*3+1],
	                          ctrlpoints[v*12+u*3+2]);
	        }
	        gl.glEnd();
	    }
	    
	    for(v=0;v<vorder;v++) {
	        gl.glBegin(GL.GL_LINE_STRIP);
	        for(u=0;u<uorder;u++) {
	            gl.glVertex3f(ctrlpoints[v*12+u*3+0],
	                          ctrlpoints[v*12+u*3+1],
	                          ctrlpoints[v*12+u*3+2]);
	        }
	        gl.glEnd();
	    }   
	    
	}
	
	
	public static void drawBezierPatch(GL2 gl, float[] points, boolean grid){
		gl.glMap2f(GL2.GL_MAP2_VERTEX_3, 0.0f, 1.0f, 3, 4, 0.0f, 1.0f, 12, 4, points, 0);
		gl.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);
		if(grid)
		    gl.glEvalMesh2(GL2.GL_LINE, 0, 20, 0, 20);
		else
		    gl.glEvalMesh2(GL2.GL_FILL, 0, 20, 0, 20);
	}

	/**
	 * Generates a flat (X-Z plane) 4x4 mesh
	 * 
	 * @return the mesh
	 */
	public static float[] getFlatMesh(){
		return new float[]  {
            -1.5f, 0.0f, -1.5f, 	-0.5f, 0.0f, -1.5f,		0.5f, 0.0f, -1.5f,	1.5f, 0.0f, -1.5f,
            
            -1.5f ,0.0f, -0.5f, 	-0.5f, 0.0f, -0.5f,		0.5f, 0.0f, -0.5f,	1.5f, 0.0f, -0.5f,
            
            -1.5f, 0.0f, 0.5f, 		-0.5f, 0.0f, 0.5f,		0.5f, 0.0f,	0.5f, 	1.5f, 0.0f, 0.5f,
            
            -1.5f, 0.0f, 1.5f, 		-0.5f, 0.0f, 1.5f, 		0.5f, 0.0f, 1.5f, 	1.5f, 0.0f, 1.5f
		};
	}
	
	// UTILITY METHODS BELOW
	
	/**
	 * Alter the anchor points at a specific position.
	 * This doesn't set their value, but rather increases
	 * or decreases it proportionally.
	 * 
	 * @param points the array of points to work on
	 * @param i column of mesh
	 * @param o row of mesh
	 * @param x change in x
	 * @param y change in y
	 * @param z change in z
	 */
	public static void addPoint(float[] points, int i, int o, float x, float y, float z){
		points[(o * 4 + i) * 3] += x; 
		points[(o * 4 + i) * 3 + 1] += y; 
		points[(o * 4 + i) * 3 + 2] += z; 
	}
	
	/**
	 * Set the anchor points at a specific position.
	 *
	 * or decreases it proportionally.
	 * 
	 * @param points the array of points to work on
	 * @param i column of mesh
	 * @param o row of mesh
	 * @param x new x
	 * @param y new y
	 * @param z new z
	 */
	public static void setPoint(float[] points, int i, int o, float x, float y, float z){
		points[(o * 4 + i) * 3] = x; 
		points[(o * 4 + i) * 3 + 1] = y; 
		points[(o * 4 + i) * 3 + 2] = z; 
	}
	
	/**
	 * Translate the mesh by a certain amount specified
	 * by x, y and z.
	 * 
	 * @param points the array of points to work on
	 * @param x change in x
	 * @param y change in y
	 * @param z change in z
	 */
	public static void translate(float[] points, float x, float y, float z){
		for(int i = 0; i < 4; i++){
			for(int o = 0; o < 4; o++){
				addPoint(points, i, o, x, y, z);
			}
		}
	}
	
	/**
	 * Get X value of the point at the specified position
	 * @param points
	 * @param i column of mesh
	 * @param o column of mesh
	 * @return
	 */
	public static float getX(float[] points, int i, int o){
		return points[(o * 4 + i) * 3];
	}
	
	/**
	 * Get Y value of the point at the specified position
	 * @param points
	 * @param i column of mesh
	 * @param o column of mesh
	 * @return
	 */
	public static float getY(float[] points, int i, int o){
		return points[(o * 4 + i) * 3 + 1];
	}
	
	/**
	 * Get Z value of the point at the specified position
	 * @param points
	 * @param i column of mesh
	 * @param o column of mesh
	 * @return
	 */
	public static float getZ(float[] points, int i, int o){
		return points[(o * 4 + i) * 3 + 2];
	}
	
	/**
	 * Get a copy of an array of points
	 * 
	 * @param points the array to copy
	 * @return the copy
	 */
	public static float[] copyMesh(float[] points){
		return Arrays.copyOf(points, points.length);
	}
	
}
