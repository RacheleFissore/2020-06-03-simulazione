package it.polito.tdp.PremierLeague.model;

public class GiocatorePeso implements Comparable<GiocatorePeso> {
	private Player player;
	private int peso;
	
	public GiocatorePeso(Player player, int peso) {
		super();
		this.player = player;
		this.peso = peso;
	}

	
	public Player getPlayer() {
		return player;
	}


	public int getPeso() {
		return peso;
	}


	@Override
	public int compareTo(GiocatorePeso o) {
		return -(this.peso - o.peso);
	}
}
