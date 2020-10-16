package dao;

import models.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComponentDAO {

    private final Connection connection;

    public ComponentDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Component component) throws DataAccessException {
        String sql = "INSERT INTO Components (component_id, component_name, component_type, " +
                "manufacturer, performance_rating, price, cpu_family, tdp) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, component.getComponentID());
            stmt.setString(2, component.getComponentName());
            stmt.setString(3, component.getComponentType());
            stmt.setString(4, component.getManufacturer());
            stmt.setInt(5, component.getPerformanceRating());
            stmt.setInt(6, component.getPrice());
            stmt.setString(7, component.getCpuFamily());
            stmt.setInt(8, component.getTpd());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the Components table in database");
        }
    }

    public Component queryNoPreconditions(String componentID) throws DataAccessException {
        Component component;
        ResultSet rs = null;
        String sql = "SELECT * FROM Components WHERE component_id = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, componentID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                component = new Component(rs.getString("component_id"),
                        rs.getString("component_name"), rs.getString("component_type"),
                        rs.getString("manufacturer"), rs.getInt("performance_rating"),
                        rs.getInt("price"), rs.getString("cpu_family),"),
                        rs.getInt("tdp"));
                return component;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while querying a component");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public List<Component> queryComponentsWithConditions(String componentType, int maxPrice, int performanceRating,
                                                         String cpuFamily) throws DataAccessException {
        List<Component> components = new ArrayList<>();
        boolean isPriced = false;
        boolean isPerformance = false;
        boolean isCPUFamily = false;
        ResultSet rs = null;
        String sql = "SELECT * FROM Components WHERE component_type = ?";
        if (maxPrice != 0) {
            isPriced = true;
            sql += " AND price <= ?";
        }
        if (performanceRating != 0) {
            isPerformance = true;
            sql += " AND performance_rating = ?";
        }
        if (cpuFamily != null) {
            isCPUFamily = true;
            sql += " AND cpu_family = ?";
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, componentType);
            if(isPriced) {
                stmt.setInt(2, maxPrice);
            }
            if(isPerformance) {
                stmt.setInt(3, performanceRating);
            }
            if(isCPUFamily) {
                stmt.setString(4, cpuFamily);
            }
            rs = stmt.executeQuery();
            while(rs.next()) {
                components.add(new Component(rs.getString("component_id"),
                        rs.getString("component_name"), rs.getString("component_type"),
                        rs.getString("manufacturer"), rs.getInt("performance_rating"),
                        rs.getInt("price"), rs.getString("cpu_family),"),
                        rs.getInt("tdp")));
            }
            return components;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error querying components with conditions: " + e.getMessage());
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int deleteAll() throws DataAccessException {
        int count;
        String sql = "DELETE FROM Components";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            count = stmt.executeUpdate();

            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting all components");
        }
    }

    public int deleteOne(String componentID) throws DataAccessException {
        int count;
        String sql = "DELETE FROM Components WHERE component = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, componentID);
            count = stmt.executeUpdate();

            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting one component");
        }
    }

}
