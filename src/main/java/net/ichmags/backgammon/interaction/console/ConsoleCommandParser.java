/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.interaction.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.ichmags.backgammon.exception.ExitException;
import net.ichmags.backgammon.game.ExitLevel;
import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.interaction.ICommand;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.interaction.pojo.LoadDiceValuesCommand;
import net.ichmags.backgammon.interaction.pojo.MoveCommand;
import net.ichmags.backgammon.interaction.pojo.PrintBoardCommand;
import net.ichmags.backgammon.interaction.pojo.PrintDiceCommand;
import net.ichmags.backgammon.interaction.pojo.RulesCommand;
import net.ichmags.backgammon.interaction.pojo.TurnBoardViewCommand;
import net.ichmags.backgammon.interaction.pojo.UndoCommand;
import net.ichmags.backgammon.l10n.LocalizationManager;
import net.ichmags.backgammon.setup.BoardView;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IDicesChoice;
import net.ichmags.backgammon.setup.IPlayer;

/**
 * The {@code ConsoleCommandParser} is a low level shell based communication interface
 * that enables a player to issue commands to the {@link IGame} for interaction.
 * A set of {@link ICommand} commands is recognized for interaction.
 * 
 * @author Anastasios Patrikis
 */
public class ConsoleCommandParser implements ICommandProvider {
	
	private BufferedReader stdin;
	private LocalizationManager l10n;
	
	/**
	 * Default constructor.
	 */
	public ConsoleCommandParser() {
		stdin = new BufferedReader(new InputStreamReader(System.in));
		l10n = LocalizationManager.get(); // just for convenience
		l10n.addBundle("net.ichmags.backgammon.interaction.console.l10n.backgammon");
	}
	
	/**
	 * Turns the {@link ConsoleCommandParser} into <i>listen</i> mode to accept and interpret
	 * user input on {@link System#in}.
	 *  
	 * @return The identified command; if the command has parameters they will be provided..
	 * @throws ExitException  in case a {@link IPlayer} requested to end the {@link IGame} before it's normal end.
	 */
	public ICommand getCommand()
	throws ExitException {
		while(true) {
			System.out.print(l10n.get("ccp.ready"));
			System.out.flush();
			String command;
			try {
				command = stdin.readLine();
				
				switch (command) {
					case "d":
					case "dice":
						return new PrintDiceCommand();
					case "b":
					case "board":
						return new PrintBoardCommand();
					case "m": 
					case "move": 
						return getMove();
					case "u": 
					case "undo": 
						return new UndoCommand();
					case "l":
					case "load":
						return getNewDiceValues();
					case "t":
					case "turn":
						return getNewBoarView();
					case "x":
					case "exit":
						doExit();
					case "r":
					case "rules":
						return new RulesCommand();
					case "h":
					case "help":
						printHelp();
						break;
					default:
						MoveCommand defaultCommand = checkDefaultCommand(command);
						if(defaultCommand != null) {
							return defaultCommand;
						}
						break;
				}
			} catch (ExitException ee) {
				throw ee;
			} catch (Exception e) {
				System.out.println(l10n.get("ccp.error", e.toString()));
			}
		}
	}
	
	@Override
	public IDices chooseDices(IDicesChoice diceChoice) {
		System.out.println(l10n.get("ccp.choose_reduced_dices"));
		
		List<IDices> dicesList = diceChoice.getAsList();
		
		for(int pos = 0; pos < dicesList.size(); pos++) {
			System.out.println(String.format("%d : %s", pos, dicesList.get(pos)));
		}
		System.out.flush();
		
		IDices selection = null;
		do {
			try {
				int option = Integer.parseInt(stdin.readLine());
				selection = dicesList.get(option);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		} while (selection == null);
		
		return selection;
	}
	
	/**
	 * Print help informations.
	 */
	private void printHelp() {
		System.out.println(l10n.get("ccp.info_dice"));
		System.out.println(l10n.get("ccp.info_board"));
		System.out.println(l10n.get("ccp.info_move"));
		System.out.println(l10n.get("ccp.info_undo"));
		System.out.println(l10n.get("ccp.info_load"));
		System.out.println(l10n.get("ccp.info_turn"));
		System.out.println(l10n.get("ccp.info_exit"));
		System.out.println(l10n.get("ccp.info_rules"));
		System.out.println(l10n.get("ccp.info_help"));
		System.out.println(l10n.get("ccp.info_confirm"));
		System.out.println(l10n.get("ccp.info_default"));
	}
	
	/**
	 * Create a {@link MoveCommand} by requesting values for
	 * <ul>
	 * <li>>the start position</li>
	 * <li>>the dice to use for moving</li>
	 * </ul>
	 *  
	 * @param command the input to interpret as move values: start positipon an dice value, separated by comma.
	 * @return
	 */
	private MoveCommand checkDefaultCommand(String command) {
		try {
			int[] ints = convert(command.split("(\\s|,)"));
			if(ints.length == 2) {
				return new MoveCommand(ints[0], ints[1]);   
			} else {
				System.out.println(l10n.get("ccp.err_move"));
			}
		} catch (Exception e) {
			System.out.println(l10n.get("ccp.err_move_ex", e.toString()));
		}
		
		return null;
	}
	
	/**
	 * Exit the current situation. More input will be requested to identify how to exit by
	 * specifying a {@link ExitLevel}.
	 * 
	 * @throws ExitException <b>ALLWAYS</b> thrown, containing the {@link ExitLevel}.
	 * @throws IOException in case there is a problem reading the input.
	 */
	private void doExit()
	throws IOException, ExitException {
		System.out.print(l10n.get("ccp.exit"));
		System.out.flush();
	    String level = stdin.readLine();
	    
	    ExitLevel exitLevel = null;
	    switch (level) {
		    case "g" : exitLevel = ExitLevel.GAME; break;
		    case "m" : exitLevel = ExitLevel.MATCH; break;
		    default: throw new IOException("Exit level unknown");
	    }
	    
	    throw new ExitException(exitLevel);
	}
	
	/**
	 * Create a {@link MoveCommand} by requesting all needed additional parameters:
	 * <ul>
	 * <li>the {@link Checker} position to play from</li>
	 * <li>the {@link IDice} to play</li>
	 * </ul>
	 * 
	 * @return A {@link MoveCommand} with all values to enable moving a {@link Checker}.
	 * @throws NumberFormatException if the entered values cannot be parsed as an {@link Integer} value.
	 * @throws IOException in case there is a problem reading the input.
	 */
	private MoveCommand getMove()
	throws NumberFormatException, IOException {
		System.out.print(l10n.get("ccp.move_form"));
		System.out.flush();
	    int from = Integer.parseInt(stdin.readLine());
		
	    System.out.print(l10n.get("ccp.move_for"));
	    System.out.flush();
	    int distance = Integer.parseInt(stdin.readLine());
		
		return new MoveCommand(from, distance);
	}
	
	/**
	 * Create a {@link TurnBoardViewCommand} by requesting all needed additional parameters:
	 * <ul>
	 * <li>the new {@link BoardView} perspective</li>
	 * </ul>
	 * 
	 * @return A {@link TurnBoardViewCommand} with the new {@link BoardView} perspective.
	 * @throws IOException in case there is a problem reading the input.
	 */
	private TurnBoardViewCommand getNewBoarView()
	throws IOException {
		System.out.print(l10n.get("ccp.board"));
		System.out.flush();
	    String newView = stdin.readLine();
	    
	    BoardView boardView = null;
	    switch (newView) {
		    case "tl" : boardView = BoardView.START_TOP_LEFT; break;
		    case "tr" : boardView = BoardView.START_TOP_RIGHT; break;
		    case "br" : boardView = BoardView.START_BOTTOM_RIGHT; break;
		    case "bl" : boardView = BoardView.START_BOTTOM_LEFT; break;
		    default: throw new IOException("New view position unknown");
	    }
	    
		return new TurnBoardViewCommand(boardView);
	}
	
	/**
	 * Create a {@link LoadDiceValuesCommand} by requesting the input of {@link IDice} values.
	 * The new values will be used by the next {@link IDices#roll()}.
	 * 
	 * @return A {@link LoadDiceValuesCommand} with a series of {@link IDice} values.
	 * @throws NumberFormatException if the entered values cannot be parsed as an {@link Integer} value.
	 * @throws IOException IOException in case there is a problem reading the input.
	 */
	private LoadDiceValuesCommand getNewDiceValues()
	throws NumberFormatException, IOException {
	    System.out.print(l10n.get("ccp.dices_input"));
		System.out.flush();
		String values = stdin.readLine();
		
		return new LoadDiceValuesCommand(convert(values.split("(\\s|,)")));
	}
	
	/**
	 * Convert a series of {@link String} values to {@link Integer} values.
	 * 
	 * @param values The {@link String} input values to convert.
	 * @return The converted {@link Integer} values.
	 * @throws NumberFormatException if the entered values cannot be parsed as an {@link Integer} value.
	 */
	private int[] convert(String[] values)
	throws NumberFormatException {
		int[] ints = new int[values.length];
		
		for(int pos = 0; pos < values.length; pos++) {
			ints[pos] = Integer.parseInt(values[pos]);
		}
		
		return ints;
	}
	
	/**
	 * Helper to check the input against a series of values, all meaning <i>true</i>.
	 *  
	 * @param yesNo the input to check
	 * @return {@code true} if the input can be interpreted as <i>true</i>.
	 */
	@SuppressWarnings("unused")
	private boolean isPositive(String yesNo) {
		return yesNo.replaceAll("^(j|ja|y|yes|t|true)$", "").isEmpty();
	}
}
