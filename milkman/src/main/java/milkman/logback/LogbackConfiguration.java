package milkman.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;
import milkman.PlatformUtil;

public class LogbackConfiguration extends ContextAwareBase implements Configurator {

	@Override
	public void configure(LoggerContext loggerContext) {
		addInfo("Setting up logback configuration.");
		setupConsoleAppender(loggerContext);
		setupFileAppender(loggerContext);
	}

	private void setupConsoleAppender(LoggerContext loggerContext) {
		ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
		ca.setContext(loggerContext);
		ca.setName("STDOUT");
		LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
		encoder.setContext(loggerContext);

		// same as
		// PatternLayout layout = new PatternLayout();
		// layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		TTLLLayout layout = new TTLLLayout();

		layout.setContext(loggerContext);
		layout.start();
		encoder.setLayout(layout);

		ca.setEncoder(encoder);
		ca.start();

		Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(ca);
	}
	
	private void setupFileAppender(LoggerContext loggerContext) {
		FileAppender<ILoggingEvent> fa = new FileAppender<ILoggingEvent>();
		fa.setFile(PlatformUtil.getWritableLocationForFile("errors.log"));
		fa.setContext(loggerContext);
		fa.setName("FILE");
		
		LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
		encoder.setContext(loggerContext);
		// same as
		PatternLayout layout = new PatternLayout();
		layout.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");

		layout.setContext(loggerContext);
		layout.start();
		encoder.setLayout(layout);
		fa.setEncoder(encoder);

		var filter = new ThresholdFilter();
		filter.setLevel("ERROR");
		fa.addFilter(filter);
		
		fa.start();

		Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(fa);
	}


}
