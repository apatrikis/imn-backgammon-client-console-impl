/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.interaction.console;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.notification.IGameStatusChangedNotificationConsumer;
import net.ichmags.backgammon.notification.INotification;
import net.ichmags.backgammon.notification.INotificationEmitter;
import net.ichmags.backgammon.notification.pojo.BoardChangedNotification;
import net.ichmags.backgammon.notification.pojo.DicesChangedNotification;
import net.ichmags.backgammon.setup.IPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer for {@link INotification} messages from a {@link INotificationEmitter}.
 * This is a simple adapter to {@link System#out}.
 * In addition, a {@link Logger} to {@link Common#LOGGER_GAMEPLAY} from the
 * logging framework is also used.
 * 
 * @author Anastasios Patrikis
 */
public class ConsoleNotificationConsumer implements IGameStatusChangedNotificationConsumer {
	
	private static Logger LOG = LoggerFactory.getLogger(Common.LOGGER_GAMEPLAY);
	
	private INotification.Level notificationMinLevel;
	
	/**
	 * Constructor.
	 *  
	 * @param notificationMinLevel the minimum {@link INotification.Level} a incoming
	 * {@link INotification} must have before it is logged to {@link System#out}
	 */
	public ConsoleNotificationConsumer(INotification.Level notificationMinLevel) {
		this.notificationMinLevel = notificationMinLevel;
	}
	
	@Override
	public void message(INotification notification) {
		if(outputLevelSatisfied(notification)) {
			System.out.println(notification);
		}
		sendToLogger(notification);
	}
	
	@Override
	public void dicesChanged(INotification notification) {
		if(outputLevelSatisfied(notification)) {
			DicesChangedNotification dicesNotification = (DicesChangedNotification)notification;
			System.out.println(dicesNotification);
		}
		sendToLogger(notification);
	}
	
	@Override
	public void boardChanged(INotification notification) {
		if(outputLevelSatisfied(notification)) {
			BoardChangedNotification boardNotification = (BoardChangedNotification)notification;
			System.out.println(boardNotification.getBoard().toString(
					boardNotification.getPlayer(), boardNotification.getGame() ));
			
			if(IPlayer.Type.COMPUTER.equals(boardNotification.getPlayer().getType())) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					LOG.error("Computer board display interrupted", e);
				}
			}
		}
//		sendToLogger(notification);
	}
	
	/**
	 * Check if the {@link INotification} has a sufficient {@link INotification.Level} for
	 * output.
	 * 
	 * @param notification the {@link INotification} whose the {@link INotification.Level}
	 * is to be checked.
	 * @return {@code true} if the {@link INotification} has at least the {@link INotification.Level}
	 * set in the constructor.
	 */
	private boolean outputLevelSatisfied(INotification notification) {
		return (notification.getLevel().ordinal() >= notificationMinLevel.ordinal());
	}
	
	/**
	 * Send the {@link INotification} to the {@link Common#LOGGER_GAMEPLAY} {@link Logger}.
	 * 
	 * @param notification the {@link INotification} that will be send to the logging
	 * framework.
	 */
	private void sendToLogger(INotification notification) {
		switch (notification.getLevel()) {
			case INFO: LOG.info(notification.toString()); break;
			case DEBUG: LOG.debug(notification.toString()); break;
			case TRACE: LOG.trace(notification.toString()); break;
		}
	}
}
