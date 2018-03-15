package ekek;

import javafx.scene.chart.PieChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.security.spec.DSAGenParameterSpec;
import java.util.List;

public class Config {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(Config.class).run();
    }
    void run(){
        String url = jdbcTemplate.execute((ConnectionCallback<String>) cbc -> cbc.getMetaData().getURL());
        System.out.println(url);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(table);
        populator.addScript(insertion);

        DataSourceInitializer init = new DataSourceInitializer();
        init.setDatabasePopulator(populator);
        DatabasePopulatorUtils.execute(populator, dataSource);

        List<String> products = jdbcTemplate.queryForList("SELECT name FROM Product",String.class);
        System.out.println(products);

        List<Product> productslist = jdbcTemplate.query("SELECT * FROM Product", (RowMapper<Product>)(rs,rowNum) -> new Product(rs.getString("name"),rs.getDouble("price")));
        System.out.println(productslist);

        ProductSet productSet = jdbcTemplate.query("SELECT * FROM Product", (ResultSetExtractor<ProductSet>) rs -> {
            ProductSet set = new ProductSet();
            while (rs.next()) set.products.add(new Product(rs.getString("name"), rs.getDouble("price")));
            return set;
        });
        System.out.println(productSet.products);

    }
    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.DERBY).build();
    }
    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }
    @Value("schema.sql")
    Resource table;
    @Value("test-data.sql")
    Resource insertion;

    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;


}
