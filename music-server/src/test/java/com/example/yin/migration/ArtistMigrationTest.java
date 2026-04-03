package com.example.yin.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Migration verification for M3 (Artist/Project schema + Singer -> Artist data migration).
 *
 * This test uses a minimal "pre-V6" schema (singer + song only) and then applies Flyway migrations
 * from V6 to V10 (baseline at V5 and target at V10).
 */
class ArtistMigrationTest {

    @Test
    void 迁移后_artist表应包含原singer数据且id保持一致_type默认为singer() throws Exception {
        DataSource ds = newH2DataSource();
        initPreV6Schema(ds);
        seedSingerAndSong(ds);

        migrateV6ToV10(ds);

        Map<Integer, String> singerNames = queryIdToName(ds, "singer");
        Map<Integer, String> artistNames = queryIdToName(ds, "artist");
        assertEquals(singerNames, artistNames, "artist 应完整包含 singer 的记录且 id/name 一致");

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM artist WHERE type <> 'singer' OR type IS NULL")) {
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1), "从 singer 迁移来的 artist.type 应默认为 'singer'");
            }
        }
    }

    @Test
    void 迁移后_song表的singer_id映射不变且artist_id应回填为singer_id() throws Exception {
        DataSource ds = newH2DataSource();
        initPreV6Schema(ds);
        seedSingerAndSong(ds);

        migrateV6ToV10(ds);

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, singer_id, artist_id FROM song ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                int rows = 0;
                while (rs.next()) {
                    rows++;
                    int singerId = rs.getInt("singer_id");
                    int artistId = rs.getInt("artist_id");
                    assertEquals(singerId, artistId, "song.artist_id 应被回填为 legacy singer_id");
                }
                assertTrue(rows > 0, "用例应至少包含一条 song 数据");
            }
        }
    }

    @Test
    void 迁移后_project与project_artist表应存在且project_artist同一角色唯一约束生效() throws Exception {
        DataSource ds = newH2DataSource();
        initPreV6Schema(ds);
        seedSingerAndSong(ds);

        migrateV6ToV10(ds);

        assertTrue(tableExists(ds, "project"), "project 表应存在");
        assertTrue(tableExists(ds, "project_artist"), "project_artist 表应存在");

        // Insert minimal project + artist, then validate unique constraint (project_id, artist_id, role)
        try (Connection c = ds.getConnection()) {
            int projectId;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO project(org_id, name) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, 1);
                ps.setString(2, "Test Project");
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    assertTrue(keys.next());
                    projectId = keys.getInt(1);
                }
            }

            // Ensure at least one artist exists (migrated from singer)
            int artistId = 1;

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO project_artist(project_id, artist_id, role) VALUES (?, ?, ?)")) {
                ps.setInt(1, projectId);
                ps.setInt(2, artistId);
                ps.setString(3, "VOCAL");
                ps.executeUpdate();
            }

            SQLException ex = assertThrows(SQLException.class, () -> {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO project_artist(project_id, artist_id, role) VALUES (?, ?, ?)")) {
                    ps.setInt(1, projectId);
                    ps.setInt(2, artistId);
                    ps.setString(3, "VOCAL");
                    ps.executeUpdate();
                }
            }, "同一 project/artist/role 重复插入应触发唯一约束");
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void 迁移后_song表应包含project_id字段() throws Exception {
        DataSource ds = newH2DataSource();
        initPreV6Schema(ds);
        seedSingerAndSong(ds);

        migrateV6ToV10(ds);

        assertTrue(columnExists(ds, "song", "project_id"), "song.project_id 字段应存在");
        assertTrue(columnExists(ds, "song", "artist_id"), "song.artist_id 字段应存在");
        assertTrue(columnExists(ds, "song", "create_time"), "song.create_time 字段应存在");
        assertTrue(columnExists(ds, "song", "update_time"), "song.update_time 字段应存在");
    }

    private static DataSource newH2DataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        String dbName = "m3_migration_" + UUID.randomUUID().toString().replace("-", "");
        ds.setURL("jdbc:h2:mem:" + dbName + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    private static void initPreV6Schema(DataSource ds) throws SQLException {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            // minimal legacy schema required by V9 (singer -> artist) and V10 (extend song)
            st.execute("CREATE TABLE singer (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "sex TINYINT DEFAULT NULL," +
                    "pic VARCHAR(255) DEFAULT NULL," +
                    "birth DATETIME DEFAULT NULL," +
                    "location VARCHAR(255) DEFAULT NULL," +
                    "introduction VARCHAR(255) DEFAULT NULL" +
                    ")");

            st.execute("CREATE TABLE song (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "singer_id INT NOT NULL," +
                    "name VARCHAR(100) NOT NULL," +
                    "introduction VARCHAR(255) DEFAULT NULL," +
                    "pic VARCHAR(255) DEFAULT NULL," +
                    "lyric VARCHAR(255) DEFAULT NULL," +
                    "url VARCHAR(255) NOT NULL" +
                    ")");
        }
    }

    private static void seedSingerAndSong(DataSource ds) throws SQLException {
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO singer(id, name, sex, pic, birth, location, introduction) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, 1);
                ps.setString(2, "Singer-A");
                ps.setInt(3, 1);
                ps.setString(4, "/a.jpg");
                ps.setString(5, "2000-01-01 00:00:00");
                ps.setString(6, "CN");
                ps.setString(7, "Intro-A");
                ps.executeUpdate();

                ps.setInt(1, 2);
                ps.setString(2, "Singer-B");
                ps.setInt(3, 0);
                ps.setString(4, "/b.jpg");
                ps.setString(5, "2001-01-01 00:00:00");
                ps.setString(6, "US");
                ps.setString(7, "Intro-B");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO song(id, singer_id, name, introduction, pic, lyric, url) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, 10);
                ps.setInt(2, 1);
                ps.setString(3, "Song-1");
                ps.setString(4, "Intro-1");
                ps.setString(5, "/s1.jpg");
                ps.setString(6, "lyric");
                ps.setString(7, "/s1.mp3");
                ps.executeUpdate();

                ps.setInt(1, 11);
                ps.setInt(2, 2);
                ps.setString(3, "Song-2");
                ps.setString(4, "Intro-2");
                ps.setString(5, "/s2.jpg");
                ps.setString(6, "lyric");
                ps.setString(7, "/s2.mp3");
                ps.executeUpdate();
            }
        }
    }

    private static void migrateV6ToV10(DataSource ds) {
        Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion(MigrationVersion.fromVersion("5"))
                .target(MigrationVersion.fromVersion("10"))
                .load()
                .migrate();
    }

    private static boolean tableExists(DataSource ds, String tableName) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = ?")) {
            ps.setString(1, tableName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1) > 0;
            }
        }
    }

    private static boolean columnExists(DataSource ds, String tableName, String columnName) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = ? AND LOWER(COLUMN_NAME) = ?")) {
            ps.setString(1, tableName.toLowerCase());
            ps.setString(2, columnName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1) > 0;
            }
        }
    }

    private static Map<Integer, String> queryIdToName(DataSource ds, String tableName) throws SQLException {
        Map<Integer, String> result = new HashMap<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name FROM " + tableName + " ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return result;
    }
}
