package me.anisekai.server.types;

import me.anisekai.api.json.BookshelfJson;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class JSONType implements UserType<BookshelfJson> {

    @Override
    public int getSqlType() {

        return Types.LONGVARCHAR;
    }

    @Override
    public Class<BookshelfJson> returnedClass() {

        return BookshelfJson.class;
    }

    @Override
    public boolean equals(BookshelfJson json1, BookshelfJson json2) {

        return (json1 == null && json2 == null) ||
                (json1 != null && json2 != null && json1.toString().equals(json2.toString()));
    }

    @Override
    public int hashCode(BookshelfJson bookshelfJson) {

        return bookshelfJson.hashCode();
    }

    @Override
    public BookshelfJson nullSafeGet(ResultSet rs, int column, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {

        String raw = rs.getString(column);
        if (raw != null) {
            return new BookshelfJson(raw);
        }

        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement pst, BookshelfJson json, int column, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {

        if (json == null) {
            pst.setNull(column, this.getSqlType());
            return;
        }
        pst.setString(column, json.toString());
    }

    @Override
    public BookshelfJson deepCopy(BookshelfJson bookshelfJson) {

        return new BookshelfJson(bookshelfJson.toMap());
    }

    @Override
    public boolean isMutable() {

        return true;
    }

    @Override
    public Serializable disassemble(BookshelfJson bookshelfJson) {

        return bookshelfJson.toString();
    }

    @Override
    public BookshelfJson assemble(Serializable serializable, Object o) {

        return new BookshelfJson((String) serializable);
    }

}
