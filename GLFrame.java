import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class GLFrame extends JFrame implements GLEventListener {
	
	public static void main(String[] s){
		new GLFrame();
	}		  
	
	private Timer timer;
	
	protected GLCanvas canvas;
	protected GLU glu;
	protected GLUT glut;
	
	private Robot robot;
	
	private float distance = 100;
	
	public float cameraX = 0;
	public float cameraY = 1;
	public float cameraZ = 0;
	
	private float lookatX = 0;
	private float lookatY = 0f;
	private float lookatZ = 0;
	
	private boolean rightPressed;
	private boolean leftPressed;
	private boolean upPressed;
	private boolean downPressed;
	
	final float SENSITIVITY = 0.005f;
	
	float speed = 0.1f;
	float alpha = (float)Math.PI;
	float beta = 0;
	
	protected boolean LIGHTS_ON = true;
	
	public GLFrame(){
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = new GLCanvas(capabilities);
		
		canvas.addKeyListener(new KeyHandler());
		canvas.addGLEventListener(this);
		this.getContentPane().add(canvas);
		
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(true);
		
		
		try {
			robot = new Robot();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB ), new Point(), null));
		canvas.requestFocusInWindow();
		
	}		
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();
				
		timer = new Timer(30);

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_MAP2_VERTEX_3);
		gl.glEnable(GL2.GL_AUTO_NORMAL);
		gl.glEnable(GL2.GL_NORMALIZE);
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glColor3f(1, 1, 1);
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    gl.glClearColor(0, 0, 0, 1);
	    
	    this.drawViewPort3(gl);
	    
		gl.glColor3f(0, 0, 1);
		GLBezier.drawShape(gl);
		
		// Boolean to toggle lights on and of. Toggle on a key press event.
		if(LIGHTS_ON){
			gl.glEnable(GL2.GL_LIGHTING); 
			gl.glEnable(GL2.GL_LIGHT0);  
			gl.glEnable(GL2.GL_NORMALIZE); 

			// Toy with these parameters. Look up specular, ambient, position and diffuse lighting.
			float[] specular = { 0.5f, 0.5f, 0.5f};
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0); 
			
			float[] ambient = { 0f, 0f, 0.2f};  // weak RED ambient 
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0); 
			
			float[] pos = {0f, 0.2f, 0f};
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
	
			float[] diffuseLight = { 1f, 1f, 1f, 1f };  // multicolor diffuse 
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0); 
		}else{
			gl.glDisable(GL2.GL_LIGHTING); 
			gl.glDisable(GL2.GL_LIGHT0);  
			gl.glDisable(GL2.GL_NORMALIZE);
		}
			
	}
	
	/**
	 * Set up the dynamic perspective view port.
	 * @param gl GL2 instance.
	 */
	private void drawViewPort3(GL2 gl){
				
		int w = getWidth() * 2;
		int h = getHeight() * 2;
		
		// Viewport 3 - Perspective
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
	    gl.glViewport(0, 0, w, h);
	    gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60, w / (double) h, 0.0001, 10000);
	    glu.gluLookAt(cameraX, cameraY, cameraZ, lookatX, lookatY, lookatZ, 0, 1, 0);	   
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
		    
	}

	@Override
	public void dispose(GLAutoDrawable drawable) { }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL2 gl = drawable.getGL().getGL2();
		 
	    gl.glViewport(0, 0, w, h);
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
	    glu.gluPerspective(45, (float) w / h, 1, 1000);
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	}
	
	public void drawFloor(GL2 gl){
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(1, 0, 0);
		gl.glVertex3f(1, 0, 1);
		gl.glVertex3f(0, 0, 1);
		gl.glEnd();
	}
	
	public void drawAxis(GL2 gl){
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(1, 0, 0); //RED - X
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(3f, 0, 0);

		gl.glColor3f(0, 1, 0); //GREEN - Y
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 3f, 0);

		gl.glColor3f(0, 0, 1); // BLUE - Z
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 0, 3f);
		gl.glEnd();
	}
	
	public void timerUpdate(){
		this.updateMovement();
		this.updateMouse();
		canvas.repaint();
	}
	
	protected void updateMouse(){
		setCameraPerspective();
		setMouseCenter();
	}
	
	private void setCameraPerspective(){
		int ox = getLocation().x + getWidth() / 2;
		int oy = getLocation().y + getHeight() / 2;
		
		int x = MouseInfo.getPointerInfo().getLocation().x;
		int y = MouseInfo.getPointerInfo().getLocation().y;
		
		alpha = alpha + (x - ox) * SENSITIVITY;
		beta = beta + (y - oy) * SENSITIVITY / 2;
		
		float dx = (float)(Math.sin(alpha) * Math.cos(beta)) * distance;
		float dz = (float)(Math.cos(alpha) * Math.cos(beta)) * distance;
		float dy = (float)(Math.sin(beta)) * distance;
				
		lookatX = dx;
		lookatY = dy;
		lookatZ = dz;
	}
	
	protected void updateMovement(){
		float dx1 = (float)(Math.sin(alpha) * Math.cos(beta)) * speed;
		float dy1 = (float)(Math.sin(beta)) * speed;
		float dz1 = (float)(Math.cos(alpha) * Math.cos(beta)) * speed;
		
		float dx2 = (float)Math.cos((float)Math.PI - alpha) * speed;
		float dy2 = 0;
		float dz2 = (float)Math.sin((float)Math.PI - alpha) * speed;
		
		if(upPressed){
			cameraX += dx1;
			lookatX += dx1;
			cameraY += dy1;
			lookatY += dy1;
			cameraZ += dz1;
			lookatZ += dz1;
		}
		if(downPressed){
	
			cameraX -= dx1;
			lookatX -= dx1;
			cameraY -= dy1;
			lookatY -= dx1;
			cameraZ -= dz1;
			lookatZ -= dx1;
		}
		if(leftPressed){
			cameraX -= dx2;
			lookatX -= dx2;
			cameraY -= dy2;
			lookatY -= dx2;
			cameraZ -= dz2;
			lookatZ -= dx2;
		}else if(rightPressed){
			cameraX += dx2;
			lookatX += dx2;
			cameraY += dy2;
			lookatY += dy2;
			cameraZ += dz2;
			lookatZ += dz2;
		}
	}
	
	private void setMouseCenter(){
		if(this.isFocused()){
			int mouse_x = getLocation().x + getWidth() / 2;
			int mouse_y = getLocation().y + getHeight() / 2;
			robot.mouseMove(mouse_x, mouse_y);
		}
	}	
	

	private class Timer implements Runnable {
		
		private long time;
		private long lastTime;
		private boolean running = true;
		
		public Timer(long time){
			this.time = time;
			new Thread(this).start();
		}
		
		public void run(){
			while(running){
				long currentTime = System.currentTimeMillis();
				if(currentTime - lastTime >= time){
					timerUpdate();
					lastTime = currentTime;
				}
			}
		}
		
	}
	
	private class KeyHandler implements KeyListener {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			if(e.getKeyCode() == KeyEvent.VK_UP){
				upPressed = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_DOWN){
				downPressed = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				leftPressed = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				rightPressed = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				System.exit(1);
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			
			if(e.getKeyCode() == KeyEvent.VK_UP){
				upPressed = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_DOWN){
				downPressed = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				leftPressed = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				rightPressed = false;
			}
			
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			if(e.getKeyChar() == 'l'){
				LIGHTS_ON = !LIGHTS_ON;
				canvas.repaint();
			}
		}
	}
}
