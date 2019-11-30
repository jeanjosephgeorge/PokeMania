package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.model.Pokemon;
import com.revature.util.ConnectionUtil;

/**
 * Pokemon DAO which handles the pokemon data. Save a pokemon to your box,
 * save your whole team, fetch a single pokemon, fetch your team, fetch your box.
 * 
 * @author Kristoffer Spencer
 */
public class PokemonDaoSql implements PokemonDao {

	private final static	PokemonDaoSql	instance		= new PokemonDaoSql();
	private final static	Logger			log				= LogManager.getLogger(PokemonDaoSql.class);
	
	private final 			String			GET_ONE_SQL		= "SELECT * FROM pokemon WHERE pokemon_id = ?",
											GET_ALL_SQL		= "SELECT * FROM pokemon WHERE trainer_id = ?",
											GET_TEAM_SQL	= "SELECT * FROM pokemon WHERE pokemon_id IN (SELECT pokemon_id FROM pokemon_team WHERE trainer_id = ?)",
											SAVE_ONE_SQL	= "INSERT INTO pokemon (trainer_id, pokedex_id, pokemon_level, "
															+ "pokemon_hp, pokemon_att, pokemon_def, pokemon_speed, pokemon_type1, "
															+ "pokemon_type2 front_image, back_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
											SAVE_TEST_SQL	= "INSERT INTO pokemon VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
											SAVE_TEAM_SQL	= "INSERT INTO pokemon_team VALUES (?, ?)",
											CLEAR_TEAM_SQL	= "DELETE FROM pokemon_team WHERE trainer_id = ?";
											
	private PokemonDaoSql() {};
	
	/**
	 * Fetch the singleton instance of the DAO
	 * 
	 * @return the single instance of this DAo
	 */
	public static PokemonDaoSql getInstance() {
		
		return instance;
		
	}

	/**
	 * Fetch a single pokemon from the user's box
	 * 
	 * @param id of the pokemon
	 * @return The pokemon
	 * @exception Throws a SQLException if an issue happens when talking to the db
	 */
	@Override
	public Pokemon fetchPokemon(int id) throws SQLException {
		
		PreparedStatement	ps;
		ResultSet			rs;
		Pokemon				pokemon		= null;
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(GET_ONE_SQL);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if(rs.next())
				
				pokemon = new Pokemon(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), 
									  rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), 
									  rs.getString(9), rs.getString(10), rs.getString(11), 
									  rs.getString(12));
			
			return pokemon;
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to fetch single pokemon\n" + e.getMessage());
			throw e;
			
		}
		
	}

	/**
	 * Fetch the saved team of the user
	 * 
	 * @param The user's id
	 * @return The last team the user had saved
	 * @exception Throws a SQLException if an issue happens when talking to the db
	 */
	@Override
	public Pokemon[] fetchTeam(int userID) throws SQLException {
		
		PreparedStatement	ps;
		ResultSet			rs;
		List<Pokemon>		pokemon		= new ArrayList<>();
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(GET_TEAM_SQL);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
			
			while(rs.next())
				
				pokemon.add(new Pokemon(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), 
						  				rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), 
						  				rs.getString(9), rs.getString(10), rs.getString(11), 
						  				rs.getString(12)));
			
			return pokemon.toArray(new Pokemon[0]);
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to fetch team\n" + e.getMessage());
			throw e;
			
		}
				
	}

	/**
	 * Fetch all the pokemon the user owns
	 * 
	 * @param The user's id
	 * @return All the pokemon the user owns
	 * @exception Throws a SQLException if an issue happens when talking to the db
	 */
	@Override
	public Pokemon[] fetchBox(int userID) throws SQLException {
		
		PreparedStatement	ps;
		ResultSet			rs;
		List<Pokemon>		pokemon		= new ArrayList<>();
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(GET_ALL_SQL);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
			
			while(rs.next())
				
				pokemon.add(new Pokemon(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), 
										rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), 
										rs.getString(9), rs.getString(10), rs.getString(11), 
										rs.getString(12)));
			
			return pokemon.toArray(new Pokemon[0]);
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to fetch all box pokemon\n" + e.getMessage());
			throw e;
			
		}
		
	}

	/**
	 * Save a single pokemon to the db
	 * 
	 * @param The pokemon to save
	 * @return Whether save was successful
	 * @exception Throws a SQLException if an issue happens when talking to the db
	 */
	@Override
	public boolean savePokemon(Pokemon pokemon) throws SQLException {
		
		PreparedStatement	ps;
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(SAVE_ONE_SQL);
			ps.setInt(1, pokemon.getTrainerId());
			ps.setInt(2, pokemon.getDexNum());
			ps.setInt(3, pokemon.getLevel());
			ps.setInt(4, pokemon.getHp());
			ps.setInt(5, pokemon.getAtt());
			ps.setInt(6, pokemon.getDef());
			ps.setInt(7, pokemon.getSpd());
			ps.setString(8, pokemon.getType1());
			ps.setString(9, pokemon.getType2());
			ps.setString(10, pokemon.getFrontImg());
			ps.setString(11, pokemon.getBackImg());
			
			return ps.executeUpdate() == 1;
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to save single pokemon\n" + e.getMessage());
			throw e;
			
		}
		
	}
	
	/**
	 * Save a single pokemon to the db and set the ID
	 * 
	 * @param The pokemon to save
	 * @return Whether save was successful
	 * @exception Throws a SQLException if an issue happens when talking to the db
	 */
	public boolean save_TEST_pokemon(Pokemon pokemon) throws SQLException {
		
		PreparedStatement	ps;
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(SAVE_TEST_SQL);
			ps.setInt(1, pokemon.getId());
			ps.setInt(2, pokemon.getTrainerId());
			ps.setInt(3, pokemon.getDexNum());
			ps.setInt(4, pokemon.getLevel());
			ps.setInt(5, pokemon.getHp());
			ps.setInt(6, pokemon.getAtt());
			ps.setInt(7, pokemon.getDef());
			ps.setInt(8, pokemon.getSpd());
			ps.setString(9, pokemon.getType1());
			ps.setString(10, pokemon.getType2());
			ps.setString(11, pokemon.getFrontImg());
			ps.setString(12, pokemon.getBackImg());
			
			return ps.executeUpdate() == 1;
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to save single pokemon\n" + e.getMessage());
			throw e;
			
		}
		
	}	

	/**
	 * Saves the current team of the user
	 * 
	 * @param The team to save
	 * @return Whether writing all was successful
	 * @exception Throws a SQLException if an issue happens when talking to the db
	 */
	@Override
	public boolean saveTeam(Pokemon[] pokemon) throws SQLException {
		
		PreparedStatement	ps;
		
		clearTeam(pokemon[0].getTrainerId());
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(SAVE_TEAM_SQL);
			
			//Add each pokemon to a batch for an efficient whole team write
			for(Pokemon poke : pokemon) {
				
				ps.setInt(1, poke.getTrainerId());
				ps.setInt(2, poke.getId());
				ps.addBatch();
				ps.clearParameters();
				
			}

			if(ps.executeBatch() != null)
				
				return true;
			
			return false;
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to save team\n" + e.getMessage());
			throw e;
			
		}
		
	}
	
	/**
	 * Called before saving a team to wipe the state so that it can be overwritten
	 * 
	 * @param userID The id of the logged in user
	 * @throws SQLException Thrown if the DB has an issue
	 */
	private void clearTeam(int userID) throws SQLException {
		
		PreparedStatement	ps;
		
		try(Connection c = ConnectionUtil.getConnection()) {
			
			ps = c.prepareStatement(CLEAR_TEAM_SQL);
			ps.setInt(1, userID);
			ps.executeUpdate();
			
		} catch(SQLException e) {
			
			log.warn("Error: Failed to clear last saved team\n" + e.getMessage());
			throw e;
			
		}
		
	}
	
}
