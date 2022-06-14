package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertici(double goal, Map<Integer, Player> idMap) {
		String sql = "SELECT DISTINCT PlayerID "
				+ "FROM actions "
				+ "GROUP BY PlayerID "
				+ "HAVING AVG(Goals) > ?";
		
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, goal);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = idMap.get(res.getInt("PlayerID"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(double goal, Map<Integer, Player> idMap) {
		String sql = "SELECT a1.PlayerID AS p1, a2.PlayerID AS p2, SUM(a1.TimePlayed) AS t1, SUM(a2.TimePlayed) AS t2 "
				+ "FROM actions AS a1, actions AS a2 "
				+ "WHERE a1.PlayerID > a2.PlayerID "
				+ "AND a1.TeamID <> a2.TeamID "
				+ "AND a1.MatchID = a2.MatchID "
				+ "AND a1.PlayerID IN (SELECT DISTINCT PlayerID "
				+ "							FROM actions "
				+ "							GROUP BY PlayerID "
				+ "							HAVING AVG(Goals) > ?) "
				+ "AND a2.PlayerID IN (SELECT DISTINCT PlayerID "
				+ "							FROM actions "
				+ "							GROUP BY PlayerID "
				+ "							HAVING AVG(Goals) > ?) "
				+ "AND a1.starts = 1 "
				+ "AND a2.starts = 1 "
				+ "GROUP BY a1.PlayerID, a2.PlayerID "
				+ "HAVING COUNT(*) >= 1";
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, goal);
			st.setDouble(2, goal);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player1 = idMap.get(res.getInt("p1"));
				Player player2 = idMap.get(res.getInt("p2"));
				
				int peso1 = res.getInt("t1");
				int peso2 = res.getInt("t2");
				int peso = 0;
				
				if(peso1 > peso2) {
					peso = peso1 - peso2;
				}
				else {
					peso = peso2 - peso1;
				}
				
				result.add(new Adiacenza(player1, player2, peso1, peso2, peso));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
