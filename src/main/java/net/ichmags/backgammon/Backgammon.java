/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon;

import java.util.List;

import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.game.IMatch;
import net.ichmags.backgammon.interaction.console.ConsoleCommandParser;
import net.ichmags.backgammon.interaction.console.ConsoleNotificationConsumer;
import net.ichmags.backgammon.notification.INotification.Level;
import net.ichmags.backgammon.reflection.ClassByTypeFinder;
import net.ichmags.backgammon.reflection.ClassListByTypeFinder;
import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IPlayer;

/**
 * This class contains the {@link #main(String[])} method to start a
 * <b>Backgammon</b> game on the {@code Console}.
 * 
 * @author Anastasios Patrikis.
 */
public class Backgammon {
	/**
	 * Main method to start the {@code Backgammon} {@code IMatch}.
	 * 
	 * @param args the arguments for application configuration (currently ignored)
	 */
	public static void main(String[] args) {
		IPlayer player1 = getPlayerInstance();
		player1.initialize("Developer", IPlayer.ID.ONE, IPlayer.Type.LOCAL, IPlayer.Level.PRO, CheckerColor.WHITE);
		
		IPlayer player2 = getPlayerInstance();
		player2.initialize("Computer", IPlayer.ID.TWO, IPlayer.Type.COMPUTER, IPlayer.Level.PRO, CheckerColor.BLACK);
		
		IMatch match = getMatchInstance();
		match.start(player1, player2, 7, getGames(), true,
				new ConsoleCommandParser(), new ConsoleNotificationConsumer(Level.INFO));
	}
	
	/**
	 * Create a instance dynamically, as an implementing class is not
	 * in this package but somewhere on the {@code classpath}.
	 * 
	 * @return a new {@link IPlayer} {@link Instance}.
	 */
	private static IPlayer getPlayerInstance() {
		return new ClassByTypeFinder<IPlayer>(IPlayer.class, true, "net\\.ichmags\\.backgammon\\..*").getInstance();
	}
	
	/**
	 * Create a instance dynamically, as an implementing class is not
	 * in this package but somewhere on the {@code classpath}.
	 * 
	 * @return a new {@link IMatch} instance.
	 */
	private static IMatch getMatchInstance() {
		return new ClassByTypeFinder<IMatch>(IMatch.class, true, "net\\.ichmags\\.backgammon\\..*").getInstance();
	}
	
	/**
	 * Create a instance dynamically, as an implementing class is not
	 * in this package but somewhere on the {@code classpath}.
	 * 
	 * @return a new {@link List} of {@link IGame} classes.
	 */
	private static List<Class<IGame>> getGames() {
		return new ClassListByTypeFinder<IGame>(IGame.class, true, "net\\.ichmags\\.backgammon\\.game\\.impl\\..*").get();
	}
}
