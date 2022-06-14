package it.polito.tdp.PremierLeague.model;

public class Adiacenza implements Comparable<Adiacenza> {
	private Player p1;
	private Player p2;
	private int durata1;
	private int durata2;
	private int peso;
	
	public Adiacenza(Player p1, Player p2, int durata1, int durata2, int peso) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.durata1 = durata1;
		this.durata2 = durata2;
		this.peso = peso;
	}

	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	public int getDurata1() {
		return durata1;
	}

	public void setDurata1(int durata1) {
		this.durata1 = durata1;
	}

	public int getDurata2() {
		return durata2;
	}

	public void setDurata2(int durata2) {
		this.durata2 = durata2;
	}

	@Override
	public int compareTo(Adiacenza o) {
		return -(this.peso - o.peso);
	}
	
}
