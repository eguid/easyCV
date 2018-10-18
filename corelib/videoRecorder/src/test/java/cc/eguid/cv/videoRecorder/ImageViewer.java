package cc.eguid.cv.videoRecorder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

/**
 * 图像浏览器
 * 
 * @author eguid
 *
 */
public class ImageViewer extends JFrame {
	/**
	 * 默认像素格式
	 */
	private int pixelfmt = BufferedImage.TYPE_3BYTE_BGR;
	private JLabel label;
	JDialog dialog=null;
	private ImageIcon icon = null;
	BufferedImage image=null;
	ByteBuffer buf=null;
	private FFmpegVideoImageGrabber grabber;//视频图像抓取器
	
	public ImageViewer(int width, int height, int pixelfmt) {
		this.pixelfmt = pixelfmt;
		super.setSize(width, height);
		label = new JLabel();
		init();
	}

	private void init() {
		setTitle("实时图像预览");
		setLayout(new BorderLayout());
		initMenu();
		icon = new ImageIcon();
		label.setSize(getWidth(), getHeight());
		label.setIcon(icon);
		add(label);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);
		Timer timer=new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				label.repaint();
			}
		}, 0, 1000/25);
		
	}
	
	private void initMenu() {
		//菜单栏
		JMenuBar bar = new JMenuBar();
		//主菜单
		JMenu menu=new JMenu("打开");
		menu.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showDialog();
			}
		});
		bar.add(menu);
		setJMenuBar(bar);
	}

	@Override
	public void setSize(int width, int height) {
		if(width!=getWidth()&&height!=getHeight()) {
			super.setSize(width, height);
			label.setSize(width,height);
		}
	}
	
	private void showDialog(){
		this.setVisible(false);
		if(dialog==null) {
			dialog = new JDialog(this, true);
			dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			dialog.setSize(400,150);
			dialog.setTitle("打开媒体");
			dialog.setLayout(new BorderLayout());
			dialog.add(getNamePwdPandel(),BorderLayout.CENTER);
			dialog.setLocationRelativeTo(this);
		}
		dialog.setVisible(true); //显示对话框，窗口阻塞，不往下执行，只有等到对话框关闭了才往下执行。
	}

	private Component getNamePwdPandel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel jlabel = new JLabel("输入网络视频源URL");
		JTextField srcText = new JTextField(30);
		JButton button=new JButton("播放");
		button.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String src=srcText.getText();
				if(src!=null&&src.length()>0&&!"".equals(src.trim())) {
					dialog.setVisible(false);
					setVisible(true);
					grabber.setUrl(src);
					try {
						grabber.grabBuffer();
					} catch (IOException e1) {
						
					}
							
				}
			}
		});
		panel.add(jlabel);
		panel.add(srcText);
		panel.add(button);
		return panel;
	}
	
	public void show(BufferedImage img) {
		icon.setImage(img);
//		label.setSize(img.getWidth(), img.getHeight());
//		label.repaint();
	}

	public void show(ByteBuffer src) {
		BufferedImage img = BGR2BufferedImage(src,getWidth(), getHeight());
		show(img);
	}
	
	public void show(ByteBuffer src,int width,int height) {
		// BufferedImage image = new BufferedImage(width, height, pixelfmt);
		BufferedImage img = BGR2BufferedImage(src, width, height);
		show(img);
	}


	/**
	 * 24位BGR转BufferedImage
	 * 
	 * @param src
	 *            -源数据
	 * @param width
	 *            -宽度
	 * @param height-高度
	 * @return
	 */
	public BufferedImage BGR2BufferedImage(ByteBuffer src, int width, int height) {
		if(image==null) {
			image = new BufferedImage(width, height, pixelfmt);
			Raster ra = image.getRaster();
			DataBufferByte db = (DataBufferByte) ra.getDataBuffer();
			buf=ByteBuffer.wrap(db.getData()).put(src);
		}else {
			buf.flip();
			buf.put(src);
		}
		
		return image;
	}

	public void setGrabber(FFmpegVideoImageGrabber grabber) {
		this.grabber=grabber;
	}
}
