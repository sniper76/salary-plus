package ag.act.itutil.dbcleaner;

import ag.act.configuration.initial.BoardStockLoader;
import ag.act.configuration.initial.SuperAdminLoader;
import ag.act.configuration.security.ActUserProvider;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Transactional
public class DbCleaner {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BoardStockLoader boardStockLoader;
    @Autowired
    private SuperAdminLoader superAdminLoader;

    public void clean() {
        clearTables(
            Arrays.stream(TableNames.values())
                .map(TableNames::getValue)
                .toArray(String[]::new)
        );

        ActUserProvider.cleanupSystemUser();
        superAdminLoader.load();
        boardStockLoader.load();
    }

    private void clearTables(String... tableNames) {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        Arrays.stream(tableNames)
            .forEach(tableName ->
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate()
            );
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
