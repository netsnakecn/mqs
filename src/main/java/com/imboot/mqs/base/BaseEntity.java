package com.imboot.mqs.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imboot.mqs.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

import java.io.*;
import java.util.Objects;


@Data
@Slf4j
public class BaseEntity implements Serializable {
    protected static final long serialVersionUID = -1L;
    @Id
    protected long id = 0L;

    protected int status;
    protected long version;

    public BaseEntity() {
    }


    public boolean isCacheable() {
        return false;
    }

    @JsonIgnore
    public static String getEntityType(Class<?> clazz) {
        return StringUtils.uncapitalize(clazz.getSimpleName());
    }
    @JsonIgnore
    public String getEntityType() {
        return getEntityType(this.getClass());
    }




    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            BaseEntity other = (BaseEntity)obj;
            System.out.println(this.id + "<=>" + other.id);
            return this.id != 0L && other.id != 0L && this.id == other.id;
        }
    }

    public int hashCode() {
        int hash = Objects.hashCode(this.id);
        return hash;
    }

    public BaseEntity clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException var6) {
            var6.printStackTrace();
        }

        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(this);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (BaseEntity)in.readObject();
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    public String getObject() {
        return getEntityType(this.getClass());
    }

    public long incrVersion() {
        ++this.version;
        return this.version;
    }


    public String toString() {
        String var10000 = getEntityType(this.getClass());
        return var10000 + "#" + this.getId();
    }

}
