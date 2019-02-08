package com.github.wreulicke.disruptor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

@Slf4j
public class DisruptorTest {
	
	@Test
	public void test() throws InterruptedException, TimeoutException {
		Disruptor<ValueEvent> disruptor = new Disruptor<>(ValueEvent.EVENT_FACTORY, 256, DaemonThreadFactory.INSTANCE);
		disruptor.setDefaultExceptionHandler(new FatalExceptionHandler());
		disruptor.handleEventsWith(new ValueEventEventHandler());
		RingBuffer<ValueEvent> ringBuffer = disruptor.start();
		for (int i = 0; i < 10000; i++) {
			ringBuffer.publishEvent((event, sequence, value) -> {
				log.info("event:{}", event);
				event.setValue(value);
			}, i);
			
		}
	}
	
	@ToString
	static final class ValueEvent {
		
		@Getter
		@Setter
		private long value;
		
		public final static EventFactory<ValueEvent> EVENT_FACTORY = ValueEvent::new;
	}
	
	@Slf4j
	private static class ValueEventEventHandler implements EventHandler<ValueEvent> {
		
		@Override
		public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) throws Exception {
			log.info("event: {}, sequence:{}, endOfBatch:{}", event, sequence, endOfBatch);
		}
	}
}
