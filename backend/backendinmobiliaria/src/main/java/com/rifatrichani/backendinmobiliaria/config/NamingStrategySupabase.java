package com.rifatrichani.backendinmobiliaria.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/*En Supabase algunas tablas y columnas tienen mayusculas o tildes ("Inmueble", "isVendido", "ubicación").
La estrategia por defecto de Spring pasa todo a minusculas incluso entre comillas, lo que rompe esos nombres.
Esta estrategia deja intactos los nombres escritos entre comillas en @Table/@Column y al resto
le aplica la conversion normal (fechaVisto -> fecha_visto)*/
public class NamingStrategySupabase extends PhysicalNamingStrategySnakeCaseImpl {

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name != null && name.isQuoted() ? name : super.toPhysicalTableName(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name != null && name.isQuoted() ? name : super.toPhysicalColumnName(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name != null && name.isQuoted() ? name : super.toPhysicalSequenceName(name, jdbcEnvironment);
    }
}
