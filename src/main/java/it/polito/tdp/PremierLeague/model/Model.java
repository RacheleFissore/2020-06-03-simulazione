package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private PremierLeagueDAO dao;
	private List<Player> dreamTeam;
	private int gradoTitMax;
	
	public Model() {
		dao = new PremierLeagueDAO();
		idMap = new HashMap<>();
		dreamTeam = new ArrayList<>();
		gradoTitMax = 0;
		
		for(Player player : dao.listAllPlayers()) {
			idMap.put(player.getPlayerID(), player);
		}
	}
	
	public void creaGrafo(double goal) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, dao.getVertici(goal, idMap));
		for(Adiacenza adiacenza : dao.getArchi(goal, idMap)) {
			if(adiacenza.getPeso() != 0) {
				if(adiacenza.getDurata1() > adiacenza.getDurata2()) {
					Graphs.addEdgeWithVertices(grafo, adiacenza.getP1(), adiacenza.getP2(), adiacenza.getPeso());
				}
				else {
					Graphs.addEdgeWithVertices(grafo, adiacenza.getP2(), adiacenza.getP1(), adiacenza.getPeso());
				}
			}
			
		}
	}
	
	public Integer getNVertici() {
		return grafo.vertexSet().size();
	}
	 
	public Integer getNArchi() {
		return grafo.edgeSet().size();
	}
	
	public String topPlayer() {
		// Devo trovare il giocatore con il maggior numero di archi uscenti perchè vorrà dire che sarà quello che avrà giocato contro più avversari.
		Player top = null;
		String result = "";
		int numMax = -1;
		List<GiocatorePeso> list = new ArrayList<>();
		
		for(Player player : grafo.vertexSet()) {
			int numGAvv = grafo.outgoingEdgesOf(player).size();
			if(numGAvv > numMax) {
				numMax = numGAvv;
				top = player;
			}
		}
		
		result += "TOP PLAYER: " + top.toString() + "\n\nAVVERSARI BATTUTI:\n";
		for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(top)) {
			list.add(new GiocatorePeso(Graphs.getOppositeVertex(grafo, edge, top), (int)grafo.getEdgeWeight(edge)));
		}
		
		Collections.sort(list);
		
		for(GiocatorePeso giocatorePeso : list) {
			result += giocatorePeso.getPlayer().toString() + " | " + giocatorePeso.getPeso() + "\n";
		}
		
		return result;
	}
	
	public String getDreamTeam(int numG) {
		List<Player> parziale = new ArrayList<>();
		String string = "";
		
		cerca(parziale, numG, new ArrayList<>(grafo.vertexSet()));
		
		
		string += "Grado di titolarità: " + gradoTitMax;
		for(Player player : dreamTeam) {
			string += "\n" + player.toString();
		}
		
		return string;
	}

	private void cerca(List<Player> parziale, int numG, List<Player> giocatori) {
		if(parziale.size() == numG) {
			int pesoUscenti = 0;
			int pesoEntranti = 0;
			
			for(Player player : parziale) {
				
				for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(player)) {
					pesoUscenti += grafo.getEdgeWeight(edge);
				}
				
				
				for(DefaultWeightedEdge edge : grafo.incomingEdgesOf(player)) {
					pesoEntranti += grafo.getEdgeWeight(edge);
				}
			}
			
			if((pesoUscenti-pesoEntranti) > gradoTitMax) {
				gradoTitMax = (pesoUscenti-pesoEntranti);
				dreamTeam = new ArrayList<>(parziale);
			}
			return;
		}
		
		for(Player player : giocatori) {
			if(!parziale.contains(player)) {
				parziale.add(player);
				
				
				// Tutti i giocatori che sono successori del giocatore considerato avranno un numero di minuti giocati inferiore perchè il 
				// grafo è stato costruito in modo che l'arco sia diretto dal giocatore con un numero di minuti giocati maggiore verso il 
				// giocatore con un numero di minuti giocati minore. Quindi rimuovo dall'elenco di giocatori inseribili nel dream team tutti i 
				// giocatori che sono successori di quello inserito nel dream team
				List<Player> rimanenti = new ArrayList<>(giocatori);
				rimanenti.removeAll(Graphs.successorListOf(grafo, player));
				
				cerca(parziale, numG, rimanenti);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
}
