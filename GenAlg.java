/*
 * Tyler Alway
 * 25 October 2016
 * CS 441
 * Lab 2
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


public class GenAlg {
	
	static int maxPop = 20; 						//Variable to determine the max population
	static int mutChance = 100;						//Variable to determine the chance of mutation

		
	public class Individual implements Comparator<Individual> {
		
		int[] chromX;
		int[] chromY;
		int[] chromZ;
		double fit;
		
		public Individual() {
			fit = 0;
		}
		
		public Individual(int[] x, int[] y, int[] z) {
			chromX = x;
			chromY = y;
			chromZ = z;
			fit = 0;
		}
		
		public void print() {
			System.out.print("X: ");
			for(int i = 0; i < chromX.length; i++) {
				System.out.print(chromX[i]); 
			}
			
			System.out.print(" Y: ");
			
			for(int i = 0; i < chromY.length; i++) {
				System.out.print(chromY[i]); 
			}
			
			System.out.print(" Z: ");
			
			for(int i = 0; i < chromZ.length; i++) {
				System.out.print(chromZ[i]); 
			}
			
			System.out.println("    fitness: " + fit);
		}

		@Override
		public int compare(Individual o1, Individual o2) {
			return (int)(o1.fit - o2.fit);
		}
	}
	
	
	//Global variables
	ArrayList<Individual> pop = new ArrayList<>();
	int gen;										//Number to track the current generation
	Random rand; 									//Variable for the random number generator
	
	
	//Main function to start the program
	public static void main(String args[]) {
		new GenAlg().run(); 
	}
	

	//Method to run the program
	public void run() {
		init();
		fit();
		printState();
		
		while(avgFit() != 0) {
			reproduce();
			fit();
			kill();
			printState();
		}
	}
	
	
	//Method to reproduce the entire population
	public void reproduce() {
		
		int startSize = pop.size(); 
		int[] x; 
		int[] y; 
		int[] z; 
		Individual temp; 
		
		// the random spot in the array that will be spliting chromosomes
		//int split = rand.nextInt(8) + 1; 
		//For now always split the chromosomes in half when corssing
		int split = 5;
		
		//Increment the generation Number
		gen++;
		
		//Cross each individual with every other individual. 
		for(int i = 0; i < startSize; i++) {
			for(int j = 0; j < startSize; j++) {
				//Don't cross with self
				if (i != j) {
					//create the offspring
					x = new int[10]; 
					y = new int[10];
					z = new int[10];
					//fill the new offspring
					for(int start = 0; start < split; start++) {
						x[start] = pop.get(i).chromX[start];
						y[start] = pop.get(i).chromY[start];
						z[start] = pop.get(i).chromZ[start];
					}
					
					for(int end = split; end < 10; end++) {
						x[end] = pop.get(i).chromX[end];
						y[end] = pop.get(i).chromY[end];
						z[end] = pop.get(i).chromZ[end];
					}

					temp = new Individual(x, y, z); 
					
					pop.add(temp);
					
					//Small chance of a random mutation in the child
					mutate(pop.size()-1);
				}	
			}	
		}
	}

	
	// Method to mutate at a random chance
	public void mutate(int index) {
		int mut = 0;
		
		//1 in mutChance chance of mutations x chrom
		if(rand.nextInt(mutChance) == 0) {
			mut = rand.nextInt(10);
			if(pop.get(index).chromX[mut] == 0) {
				pop.get(index).chromX[mut] = 1;
			}
			else {
				pop.get(index).chromX[mut] = 0;
			}
		}
		
		//1 in 10 chance of mutations y chrom
		if(rand.nextInt(10) == 0) {
			mut = rand.nextInt(10);
			if(pop.get(index).chromY[mut] == 0) {
				pop.get(index).chromY[mut] = 1;
			}
			else {
				pop.get(index).chromY[mut] = 0;
			}
		}
		
		//1 in 10 chance of mutations z chrom
		if(rand.nextInt(10) == 0) {
			mut = rand.nextInt(10);
			if(pop.get(index).chromZ[mut] == 0) {
				pop.get(index).chromZ[mut] = 1;
			}
			else {
				pop.get(index).chromZ[mut] = 0;
			}
		}
	}
	
	
	//converts binary with sign number to decimal and returns the abs decimal value
	// Number are in the format sign bit + 9-bit number
	public int toDec(int [] num) {
		
		double mag = 0;
		int total = 0;
		
		for(int i = 9; i > 0; i--) {
			if(num[i] != 0) {
				total += (int) Math.pow(2, mag);
			}
			mag++;
		}

		return total;
	}
	
	
	//Calculates the fitness of the given individual
	public void fit() {
		
		//assign each individual a fitness.
		//This will be determined by the magnitude of each individuals chromosomes
		for (Individual x: pop){
			x.fit = Math.pow(toDec(x.chromX), 2) +Math.pow(toDec(x.chromY), 2) + Math.pow(toDec(x.chromZ), 2);
		}
		
		//Sort the population by their fitness
		Collections.sort(pop, new Individual());
	}
	
	
	//Prints the avg fitness for the current population
	public double avgFit() {
		double avg = 0;
		
		//Calculate the average fitness
		
		for (Individual x: pop){
			avg += x.fit;
		}
		
		avg = avg/pop.size();
		
		return avg; 
	}
	
	
	//Reduces the population down to maxPop size
	public void kill() {
				
		//move the surviving population to a temp 
		ArrayList<Individual> temp = new ArrayList<>();
		for(int i = 0; i < maxPop; i++) {
			temp.add(pop.get(i));
		}
		
		//clears the old list
		pop.clear();
		
		//sets pop to the new list
		pop = temp;
	}
	

	//Initializes the population 
	public void init() {
		rand = new Random();
		gen = 0;
		
		// Generate a new population of maxPop 
		for(int i = 0; i < maxPop; i++) {
			// Create an new array of 10 ints and fill it
			Individual temp = new Individual (new int[10], new int[10], new int[10]);
			
			for(int j = 0; j < 10; j++) {
				temp.chromX[j] = rand.nextInt(2);
			}
			
			for(int j = 0; j < 10; j++) {
				temp.chromY[j] = rand.nextInt(2);
			}
			
			for(int j = 0; j < 10; j++) {
				temp.chromZ[j] = rand.nextInt(2);
			}
			
			pop.add(temp);
		}
		
	}
	
	
	//prints the current state 
	public void printState() {
		System.out.println("Generaton Number: " + gen);
	
		//print the average fitness
		System.out.println("Average fitness: " + avgFit());
		
		for(int i = 0; i < pop.size(); i++) {
			pop.get(i).print();
		}
		
		System.out.println();
	}
	
}
