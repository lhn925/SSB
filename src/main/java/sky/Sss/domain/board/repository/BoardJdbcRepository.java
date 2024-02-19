/*
package sky.board.domain.board.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import sky.board.domain.board.entity.Board;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
//@Repository
public class BoardJdbcRepository implements BoardRepository {

    private final DataSource dataSource;
    private Connection conn = null;
    private PreparedStatement pstm = null;
    private ResultSet rs = null;

    public BoardJdbcRepository (DataSource dataSource) {
        log.info("datasource {} ", dataSource);
        this.dataSource = dataSource; // 의존 주입
    }

    @Override
    public void add (Board board) {
        try {
            getConn();
            pstm = conn.prepareStatement("INSERT INTO board(title,text,nickname) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, board.getTitle());
            pstm.setString(2, board.getText());
            pstm.setString(3, board.getNickname());
            pstm.executeUpdate();
            rs = pstm.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("글쓰기 등록 문제 발생");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstm, rs);
        }
    }

    @Override
    public Optional<Board> findOne (Long findId) {
        Board board = null;
        try {
            getConn();
            pstm = conn.prepareStatement("select* from board where id = ?");
            pstm.setLong(1, findId);
            rs = pstm.executeQuery();


            if (rs.next()) {
                board = new Board();
                board.setId(rs.getLong("id"));
                board.setTitle(rs.getString("title"));
                board.setText(rs.getString("text"));
                board.setNickname(rs.getString("nickname"));
                board.setDate(rs.getDate("date"));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstm, rs);
        }
        return Optional.ofNullable(board);
    }

    @Override
    public List<Board> findByList (int page, int end) {
        int start = 10 * (page - 1);
        List<Board> boardList = new ArrayList<>();
        try {
            getConn();
            pstm = conn.prepareStatement("select* from board order by date asc Limit ? , ?");
            pstm.setInt(1, start);
            pstm.setInt(2, end);
            rs = pstm.executeQuery();
            while (rs.next()) {
                Board board = new Board();
                board.setId(rs.getLong("id"));
                board.setTitle(rs.getString("title"));
                board.setText(rs.getString("text"));
                board.setNickname(rs.getString("nickname"));
                board.setDate(rs.getDate("date"));
                boardList.add(board);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstm, rs);
        }
        return boardList;
    }


    private void close (AutoCloseable... autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable != null) {
                try {
                    autoCloseable.close();
                } catch (Exception e) {
                    throw new RuntimeException("close 예외 발생");
                }
            }
        }
    }

    public void getConn () throws SQLException {
        this.conn = DataSourceUtils.getConnection(dataSource);// connection 갖고오기
    }
}
*/
