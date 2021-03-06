package com.wolf.test.netty.two;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/13
 * Time: 9:44
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class MessageServerPipelineFactory implements ChannelPipelineFactory {

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("decoder", new MessageDecoder());
		pipeline.addLast("encoder", new MessageEncoder());
//pipeline.addLast("handler", new MessageServerHandler());

		return pipeline;
	}
}
