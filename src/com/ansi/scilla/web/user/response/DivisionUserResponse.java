package com.ansi.scilla.web.user.response;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.user.query.PermissionUserLookup;
import com.ansi.scilla.web.user.query.UserLookupItem;


public class DivisionUserResponse extends MessageResponse{

    private List<DivisionUserItem> itemList;
    
	public DivisionUserResponse() {
        super();
    }

    public DivisionUserResponse(Connection conn, Integer userId) {
        this();
        makeDivisionUserList(conn, userId);
    }

    private void makeDivisionUserList(Connection conn, Integer userId) throws SQLException {
        String sql = "select division.division_id, concat(division_nbr,'-',division_code) as div, division.description, division_user.title_id "
                        + "\n from division  "
                        + "\n left outer join division_user on division_user.division_id=division.division_id and division_user.user_id=? "
                        + "\n order by div";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        this.itemList = new ArrayList<DivisionUserItem>();
        while ( rs.next() ) {
            this.itemList.add(new DivisionUserItem(rs));
        }
        rs.close();
    }

    public class DivisionUserItem extends ApplicationObject {
        private Integer divisionId;
        private String div;
        private String description;
        private Boolean active;

        public DivisionUserItem(ResultSet rs) {
            super();
            this.divisionId = rs.getInt(Division.DIVISION_ID);
            this.div = rs.getString("div");
            this.descrition = rs.getString(Division.DESCRIPTION);
            this.active = rs.getObject(DivisionUser.TITLE_ID) != null;
        }
    }
}
