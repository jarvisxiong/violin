package com.wolf.test.netty.three;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/11/3
 * Time: 14:02
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class NettyClient {

	public static void main(String[] args) {
		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the default event pipeline.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new ClientHandler());
			}
		});

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8000));

		// Wait until the connection is closed or the connection attempt fails.
		future.getChannel().getCloseFuture().awaitUninterruptibly();

		// Shut down thread pools to exit.
		bootstrap.releaseExternalResources();
	}

	private static class ClientHandler extends SimpleChannelHandler {
		private BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			if (e.getMessage() instanceof String) {
				String message = (String) e.getMessage();
				System.out.println(message);

				e.getChannel().write(sin.readLine());

				System.out.println("\n等待客户端输入。。。");
			}

			super.messageReceived(ctx, e);
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			System.out.println("已经与Server建立连接。。。。");
			System.out.println("\n请输入要发送的信息：");
			super.channelConnected(ctx, e);

			e.getChannel().write(sin.readLine());
		}
	}
}
