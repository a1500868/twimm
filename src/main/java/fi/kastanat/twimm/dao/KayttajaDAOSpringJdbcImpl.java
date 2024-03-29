package fi.kastanat.twimm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fi.kastanat.twimm.bean.Kayttaja;

@Repository
public class KayttajaDAOSpringJdbcImpl implements KayttajaDAO {

	@Inject
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Tallettaa parametrina annetun henkil�n tietokantaan. Tietokannan
	 * generoima id asetetaan parametrina annettuun olioon.
	 */
	public void talleta(Kayttaja k) {
		final String sql = "insert into kayttaja(etunimi, sukunimi, sahkoposti, kuvaus) values(?,?,?,?)";

		// anonyymi sis�luokka tarvitsee vakioina v�litett�v�t arvot,
		// jotta roskien keruu onnistuu t�m�n metodin suorituksen p��ttyess�.
		final String etunimi = k.getEtunimi();
		final String sukunimi = k.getSukunimi();
		final String sahkoposti = k.getSahkoposti();
		final String kuvaus = k.getKuvaus();

		// jdbc pist�� generoidun id:n t�nne talteen
		KeyHolder idHolder = new GeneratedKeyHolder();

		// suoritetaan p�ivitys itse m��ritellyll� PreparedStatementCreatorilla
		// ja KeyHolderilla
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql,
						new String[] { "id" });
				ps.setString(1, etunimi);
				ps.setString(2, sukunimi);
				ps.setString(3, sahkoposti);
				ps.setString(4, kuvaus);
				return ps;
			}
		}, idHolder);

		// tallennetaan id takaisin beaniin, koska
		// kutsujalla pit�isi olla viittaus samaiseen olioon
		k.setId(idHolder.getKey().intValue());

	}

	public Kayttaja etsi(int id) {
		String sql = "select id, etunimi, sukunimi, sahkoposti, kuvaus from kayttaja where id = ?";
		Object[] parametrit = new Object[] { id };
		RowMapper<Kayttaja> mapper = new KayttajaRowMapper();

		Kayttaja k;
		try {
			k = jdbcTemplate.queryForObject(sql, parametrit, mapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new KayttajaaEiLoydyPoikkeus(e);
		}
		return k;

	}

	public List<Kayttaja> haeKaikki() {

		String sql = "select id, etunimi, sukunimi, sahkoposti, kuvaus from kayttaja";
		RowMapper<Kayttaja> mapper = new KayttajaRowMapper();
		List<Kayttaja> kayttajat = jdbcTemplate.query(sql, mapper);

		return kayttajat;
	}
}
