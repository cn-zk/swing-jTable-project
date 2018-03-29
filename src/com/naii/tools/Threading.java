package com.naii.tools;

import java.io.IOException;

public abstract class Threading implements Runnable {

	private String name;				// name
	protected Thread th;				// 当前线程
	private boolean suspend;			// 暂停
	
	/**
	 * 中断通知
	 */
	private boolean interrupt;

	/**
	 * 构造一个以时间命名的线程
	 */
	public Threading() {
		// 以时间来命名
		this(String.valueOf(System.currentTimeMillis()));
	}
	
	/**
	 * 线程
	 * @param name
	 */
	public Threading(String name) {
		this.name = (name == null || name.length() < 1) ? 
				String.valueOf(System.currentTimeMillis()):
				name ;
	}

	/**
	 * Suspend 控制器
	 */
	public void run() {
		try {
			while(runner() && isInterrupt()){
				checkWait();
			}
		}catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
	
	protected void checkWait() throws InterruptedException{
		// IllegalMonitorStateException - 如果当前线程不是此对象监视器的所有者
		synchronized (this) {
			while (suspend)
				wait();
		}
	}
	
	/**
	 * 线程启动后运行的方法
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public abstract boolean runner() throws InterruptedException;
	
	/**
	 * 暂停线程
	 */
	public void suspend() {
		this.suspend = true;
	}

	/**
	 * 唤醒线程
	 */
	public synchronized void resume() {
		this.suspend = false;
		// IllegalMonitorStateException - 如果当前线程不是此对象监视器的所有者
		notify();
	}

	/**
	 * 返回线程名
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 是否暂停
	 * @return
	 */
	public boolean isSuspend() {
		return suspend;
	}
	
	/**
	 * 线程是否处于活动状态
	 */
	public boolean isAlive(){
		return th != null ? th.isAlive() : false;
	}
	
	public void interrupt() {
		interrupt = true;
		if(isAlive()){
			suspend();
			th.interrupt();
		}
	}
	
	public boolean isInterrupt() {
		return interrupt;
	}
	
	/**
	 * 启动
	 * 
	 * 1.如果线程被暂停则启动线程
	 * 2.如果为实例化线程则创建并启动线程
	 * 3.如果启动中则不进行任何操作
	 */
	public void start(){
		if(isAlive()){
			if(isSuspend()){
				resume();
			}
		}else{
			if(isSuspend()){
				resume();
			}
			th = new Thread(this, name);
			interrupt = true;
			th.start();
		}
	}

	/**
	 * 停止
	 */
	@Deprecated
	public void stop(){
		if(th != null)
			th.stop();
	}
	
	@SuppressWarnings("static-access")
	public void sleep(long millis) throws InterruptedException{
		th.sleep(millis);
	}
}