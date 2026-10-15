package co.edu.unab.stagepass.infrastructure.adapter.out.mysql;

import co.edu.unab.stagepass.application.port.out.RecintoRepository;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Evento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Adaptador secundario hacia la base legacy MySQL. Solo hace consultas (SELECT).
@Repository
public class MySqlRecintoAdapter implements RecintoRepository {

    private final JdbcTemplate jdbc;

    public MySqlRecintoAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Evento> buscarEvento(String eventoId) {
        String sql = "SELECT id, nombre, tipo, recinto_id, fecha_hora, estado FROM evento WHERE id = ?";
        List<Evento> eventos = jdbc.query(sql, (rs, fila) -> new Evento(
                rs.getString("id"),
                rs.getString("nombre"),
                Evento.Tipo.valueOf(rs.getString("tipo")),
                rs.getString("recinto_id"),
                rs.getTimestamp("fecha_hora").toLocalDateTime(),
                Evento.Estado.valueOf(rs.getString("estado"))), eventoId);
        return eventos.stream().findFirst();
    }

    @Override
    public List<Asiento> buscarAsientosDeRecinto(String recintoId) {
        String sql = "SELECT id, recinto_id, codigo, precio_base, estado FROM asiento WHERE recinto_id = ? ORDER BY codigo";
        return jdbc.query(sql, (rs, fila) -> mapearAsiento(rs), recintoId);
    }

    @Override
    public List<Asiento> buscarAsientos(List<String> asientoIds) {
        if (asientoIds.isEmpty()) {
            return List.of();
        }
        String parametros = String.join(",", Collections.nCopies(asientoIds.size(), "?"));
        String sql = "SELECT id, recinto_id, codigo, precio_base, estado FROM asiento WHERE id IN (" + parametros + ")";
        return jdbc.query(sql, (rs, fila) -> mapearAsiento(rs), asientoIds.toArray());
    }

    private Asiento mapearAsiento(ResultSet rs) throws SQLException {
        return new Asiento(
                rs.getString("id"),
                rs.getString("recinto_id"),
                rs.getString("codigo"),
                rs.getBigDecimal("precio_base"),
                Asiento.Estado.valueOf(rs.getString("estado")));
    }
}
