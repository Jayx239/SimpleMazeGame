/*
 * SimpleMazeGame.java
 * Copyright (c) 2008, Drexel University.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Drexel University nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY DREXEL UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DREXEL UNIVERSITY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package maze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import maze.ui.MazeViewer;

/**
 * 
 * @author Sunny
 * @version 1.0
 * @since 1.0
 */
public class SimpleMazeGame {
	/**
	 * Creates a small maze.
	 */

	/* Default create maze method */
	public static Maze createMaze() {

		Maze maze = new Maze();

		Room room1 = new Room(0);
		Room room2 = new Room(1);
		Door door = new Door(room1, room2);
		door.setOpen(true);

		room1.setSide(Direction.North, new Wall());
		room1.setSide(Direction.South, new Wall());
		room1.setSide(Direction.East, door);
		room1.setSide(Direction.West, new Wall());

		room2.setSide(Direction.North, new Wall());
		room2.setSide(Direction.South, new Wall());
		room2.setSide(Direction.East, new Wall());
		room2.setSide(Direction.West, door);

		maze.addRoom(room1);
		maze.addRoom(room2);
		maze.setCurrentRoom(0);
		return maze;

	}

	/*
	 * Method to load maze from file if cmd line parameter of the file path is
	 * entered
	 */
	public static Maze loadMaze(final String path) throws IOException {
		/* Key words */
		String wallWord = "wall";
		char doorChar = 'd';
		String openWord = "open";

		/* Parameter indexes */
		int paramItemType = 0;
		int paramIdNumber = 1;
		int paramNorthIndex = 2;
		int paramSouthIndex = 3;
		int paramEastIndex = 4;
		int paramWestIndex = 5;
		int doorRoom1Index = 2;
		int doorRoom2Index = 3;
		int doorOpenIndex = 4;
		int defaultStartRoom = 0;

		Maze maze = new Maze();

		BufferedReader fileReader = new BufferedReader(new FileReader(path));
		String line;
		while ((line = fileReader.readLine()) != null) {
			if (line.equals("\n"))
				continue;

			String[] params = line.split(" ");

			switch (params[paramItemType]) {
			case "room":
				int roomNumber = Integer.parseInt(params[paramIdNumber]);
				Room newRoom = maze.getRoom(roomNumber);
				if (newRoom == null) {
					newRoom = new Room(roomNumber);
					maze.addRoom(newRoom);
				}
				/* Add new wall or room if direction is not a door */
				if (params[paramNorthIndex].equals("wall")) {
					newRoom.setSide(Direction.North, new Wall());
				} else if (params[paramNorthIndex].toLowerCase().charAt(0) != 'd') {
					int sideRoomNumber = Integer.parseInt(params[paramNorthIndex]);
					Room sideRoom = maze.getRoom(sideRoomNumber);
					if (sideRoom == null)
						sideRoom = new Room(sideRoomNumber);

					maze.addRoom(sideRoom);
					newRoom.setSide(Direction.North, sideRoom);
				}

				if (params[paramSouthIndex].equals("wall"))
					newRoom.setSide(Direction.South, new Wall());
				else if (params[paramSouthIndex].toLowerCase().charAt(0) != 'd') {
					int sideRoomNumber = Integer.parseInt(params[paramSouthIndex]);
					Room sideRoom = maze.getRoom(sideRoomNumber);
					if (sideRoom == null)
						sideRoom = new Room(sideRoomNumber);

					maze.addRoom(sideRoom);
					newRoom.setSide(Direction.South, sideRoom);
				}

				if (params[paramEastIndex].equals("wall"))
					newRoom.setSide(Direction.East, new Wall());
				else if (params[paramEastIndex].toLowerCase().charAt(0) != 'd') {
					int sideRoomNumber = Integer.parseInt(params[paramEastIndex]);
					Room sideRoom = maze.getRoom(sideRoomNumber);
					if (sideRoom == null)
						sideRoom = new Room(sideRoomNumber);

					maze.addRoom(sideRoom);
					newRoom.setSide(Direction.East, sideRoom);
				}

				if (params[paramWestIndex].equals("wall"))
					newRoom.setSide(Direction.West, new Wall());
				else if (params[paramWestIndex].toLowerCase().charAt(0) != 'd') {
					int sideRoomNumber = Integer.parseInt(params[paramWestIndex]);
					Room sideRoom = maze.getRoom(sideRoomNumber);
					if (sideRoom == null)
						sideRoom = new Room(sideRoomNumber);

					maze.addRoom(sideRoom);
					newRoom.setSide(Direction.West, sideRoom);
				}

				break;
			case "door":
				/* Add door between rooms */
				int room1Number = Integer.parseInt(params[doorRoom1Index]);
				int room2Number = Integer.parseInt(params[doorRoom2Index]);
				boolean isOpen = params[doorOpenIndex].toLowerCase().equals(
						openWord);

				Room room1 = maze.getRoom(room1Number);
				Room room2 = maze.getRoom(room2Number);

				Door newDoor = new Door(room1, room2);
				
				/* Add door to each room on corresponding sides */
				for (Direction direction : Direction.values()) {
					if (room1.getSide(direction) == null) {
						room1.setSide(direction, newDoor);

						if (direction.equals(Direction.North))
							room2.setSide(Direction.South, newDoor);
						else if (direction.equals(Direction.South))
							room2.setSide(Direction.North, newDoor);
						else if (direction.equals(Direction.East))
							room2.setSide(Direction.West, newDoor);
						else
							room2.setSide(Direction.East, newDoor);
						break;
					}
				}

				break;
			}
		}
		
		/* Set current room */
		maze.setCurrentRoom(defaultStartRoom);
		System.out.println("Maze loaded from file!");
		
		return maze;
	}

	public static void main(String[] args) {
		Maze maze = null;
		if (args.length == 0)
			maze = createMaze();
		else
			try {
				maze = loadMaze(args[0]);
			} catch (IOException e) {
				System.err.println("Error generating maze from file!");
				e.printStackTrace();
			}

		MazeViewer viewer = new MazeViewer(maze);
		viewer.run();
	}
}
