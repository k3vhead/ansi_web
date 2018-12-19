package com.ansi.scilla.web.user.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.web.common.response.MessageResponse;



public class DivisionUserResponse extends MessageResponse{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<DivisionUserItem> itemList;
    
	public List<DivisionUserItem> getItemList(){
		return itemList;
	}
	
	public void setItemList(List<DivisionUserItem> itemList) {
		this.itemList = itemList;
	}
	
	public DivisionUserResponse() {
        super();
    }

    public DivisionUserResponse(Connection conn, Integer userId, Integer loginId) throws SQLException {
        this();
        makeDivisionUserList(conn, userId, loginId);
    }

    private void makeDivisionUserList(Connection conn, Integer userId, Integer loginId) throws SQLException {
        String sql = "select abc.division_id, abc.div, abc.description, abc.title_id from "
        		+ "\n (select division.division_id, concat(division_nbr,'-',division_code) as div, division.description, division_user.title_id "
        		+ "\n from division "
        		+ "\n left outer join division_user on " 
        		+ "\n division_user.division_id=division.division_id "
        		+ "\n and division_user.user_id=?) abc "
        		+ "\n inner join division_user on division_user.division_id=abc.division_id and division_user.user_id=? "
        		+ "\n order by div";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2,  loginId);
        ResultSet rs = ps.executeQuery();
        this.itemList = new ArrayList<DivisionUserItem>();
        while ( rs.next() ) {
            this.itemList.add(new DivisionUserItem(rs));
        }
        rs.close();
    }

    public class DivisionUserItem extends ApplicationObject {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Integer divisionId;
        private String div;
        private String description;
        private Boolean active;

        public DivisionUserItem(ResultSet rs) throws SQLException {
            super();
            this.divisionId = rs.getInt(Division.DIVISION_ID);
            this.div = rs.getString("div");
            this.description = rs.getString(Division.DESCRIPTION);
            this.active = rs.getObject(DivisionUser.TITLE_ID) != null;
        }

		public Integer getDivisionId() {
			return divisionId;
		}
		
		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}
		
		public String getDiv() {
			return div;
		}
		
		public void setDiv(String div) {
			this.div = div;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public Boolean getActive() {
			return active;
		}
		
		public void setActive(Boolean active) {
			this.active = active;
		}
        
    }
}
