package cn.ucai.fulicenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.fuli_center.I;
import com.fuli_center.bean.AlbumBean;
import com.fuli_center.bean.Avatar;
import com.fuli_center.bean.BoutiqueBean;
import com.fuli_center.bean.CartBean;
import com.fuli_center.bean.CategoryChildBean;
import com.fuli_center.bean.CategoryParentBean;
import com.fuli_center.bean.ColorBean;
import com.fuli_center.bean.Contact;
import com.fuli_center.bean.GoodDetailsBean;
import com.fuli_center.bean.NewGoodBean;
import com.fuli_center.bean.PropertyBean;
import com.fuli_center.bean.User;
import com.fuli_center.bean.CollectBean;
import com.fuli_center.biz.IFuLiCenterBiz;
import com.fuli_center.utils.JdbcUtils;
import com.fuli_center.utils.Utils;

/**
 * 数据访问层
 * 
 * @author yao
 */
public class FuLiCenterDao implements IFuLiCenterDao {

	@Override
	synchronized public int addCart(CartBean cart) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + IFuLiCenterBiz.Cart.TABLE_NAME + "("
				+ IFuLiCenterBiz.Cart.GOODS_ID + ","
				+ IFuLiCenterBiz.Cart.COUNT + ","
				+ IFuLiCenterBiz.Cart.USER_NAME+ ","
				+ IFuLiCenterBiz.Cart.IS_CHECKED
				+ ")values(?,?,?,?)";
		int count =0;
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, cart.getGoodsId());
			statement.setInt(2, cart.getCount());
			statement.setString(3, cart.getUserName());
			statement.setBoolean(4,cart.isChecked());
			count = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		if(count>0){
			int id=findCatLastId();
			System.out.println("dao.addCart.lastId="+id);
			return id;
		}
		return 0;
	}

	synchronized private int findCatLastId() {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select id from " + IFuLiCenterBiz.Cart.TABLE_NAME
			+" order by id desc limit 0,1";
		try {
			statement = connection.prepareStatement(sql);
			set = statement.executeQuery();
			if (set.next()) {
				int id=set.getInt(IFuLiCenterBiz.Collect.ID);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}
		return 0;
	}

	@Override
	synchronized public boolean deleteCart(int cartId) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "delete from " + IFuLiCenterBiz.Cart.TABLE_NAME
				+ " where " + IFuLiCenterBiz.Cart.ID + "=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, cartId);
			int count = statement.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	synchronized public boolean updateCart(int cartId,int count) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + IFuLiCenterBiz.Cart.TABLE_NAME + " set "
				+ IFuLiCenterBiz.Cart.COUNT + "=?" + "where "
				+ IFuLiCenterBiz.Cart.ID + "=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, count);
			statement.setInt(2, cartId);
			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	synchronized public boolean updateCart(int cartId,int count,boolean isChecked) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + IFuLiCenterBiz.Cart.TABLE_NAME + " set "
				+ IFuLiCenterBiz.Cart.COUNT + "=?,"
				+ IFuLiCenterBiz.Cart.IS_CHECKED + "=?" + " where "
				+ IFuLiCenterBiz.Cart.ID + "=?";
		System.out.println("sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, count);
			statement.setInt(2, isChecked?1:0);
			statement.setInt(3, cartId);
			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	synchronized public CartBean[] findCarts(String userName,int pageId, int pageSize) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + IFuLiCenterBiz.Cart.TABLE_NAME
			+" where "+IFuLiCenterBiz.Cart.USER_NAME+"=? limit ?,?";
		CartBean[] carts = new CartBean[0];
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set = statement.executeQuery();
			while (set.next()) {
				CartBean cart = readCart(set);
				carts=Utils.add(carts, cart);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}

		return carts;
	}

	/**
	 * ��ȡһ����¼
	 * 
	 * @param set
	 * @return
	 * @throws SQLException
	 */
	/*
	 * statement.setString(1, cart.getGood().getGoodsId());
	 * statement.setString(1, cart.getGood().getGoodsId()); statement.setInt(2,
	 * cart.getCount()); statement.setBoolean(3, cart.isChecked());
	 */
	private CartBean readCart(ResultSet set) throws SQLException {
		CartBean cart = new CartBean();
		cart.setId(set.getInt(IFuLiCenterBiz.Cart.ID));
		cart.setCount(set.getInt(IFuLiCenterBiz.Cart.COUNT));
		cart.setGoodsId(set.getInt(IFuLiCenterBiz.Cart.GOODS_ID));
		cart.setUserName(set.getString(IFuLiCenterBiz.Cart.USER_NAME));
		cart.setChecked(set.getInt(IFuLiCenterBiz.Cart.IS_CHECKED)==1);
		return cart;
	}

	@Override
	synchronized public BoutiqueBean[] findBoutiques() {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.Boutique.TABLE_NAME;
		try {
			statement=connection.prepareStatement(sql);
			set=statement.executeQuery();
			BoutiqueBean[] boutiques=new BoutiqueBean[0];
			while(set.next()){
				BoutiqueBean boutique=new BoutiqueBean();
				boutique.setDescription(set.getString(IFuLiCenterBiz.Boutique.DESCRIPTION));
				boutique.setId(set.getInt(IFuLiCenterBiz.Boutique.ID));
				boutique.setImageurl(set.getString(IFuLiCenterBiz.Boutique.IMAGE_URL));
				boutique.setName(set.getString(IFuLiCenterBiz.Boutique.NAME));
				boutique.setTitle(set.getString(IFuLiCenterBiz.Boutique.TITLE));
				boutiques=Utils.add(boutiques, boutique);
			}
			return boutiques;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public NewGoodBean[] findNewAndBoutiqueGoods(int catId, int pageId, int pageSize) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.NewAndBoutiqueGood.TABLE_NAME
			+" where "
			+IFuLiCenterBiz.NewAndBoutiqueGood.CAT_ID+"=?"
			+" limit ?,?";
		System.out.println("findNewAndBoutiqueGoods.sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, catId);
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set=statement.executeQuery();
			NewGoodBean[] goods=new NewGoodBean[0];
			while(set.next()){
				NewGoodBean good=new NewGoodBean();
				good.setAddTime(set.getLong(IFuLiCenterBiz.NewAndBoutiqueGood.ADD_TIME));
				good.setCatId(set.getInt(IFuLiCenterBiz.NewAndBoutiqueGood.CAT_ID));
				good.setColorCode(set.getString(IFuLiCenterBiz.Property.COLOR_CODE));
				good.setColorId(set.getInt(IFuLiCenterBiz.Property.COLOR_ID));
				good.setColorName(set.getString(IFuLiCenterBiz.Property.COLOR_NAME));
				good.setColorUrl(set.getString(IFuLiCenterBiz.Property.COLOR_URL));
				good.setCurrencyPrice(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.CURRENT_PRICE));
				good.setGoodsId(set.getInt(IFuLiCenterBiz.NewAndBoutiqueGood.GOODS_ID));
				good.setGoodsBrief(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.GOODS_BRIEF));
				good.setGoodsEnglishName(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.GOODS_ENGLISH_NAME));
				good.setGoodsImg(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.GOODS_IMG));
				good.setGoodsName(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.GOODS_NAME));
				good.setGoodsThumb(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.GOODS_THUMB));
				good.setId(set.getInt(IFuLiCenterBiz.NewAndBoutiqueGood.ID));
				good.setPromote(set.getInt(IFuLiCenterBiz.NewAndBoutiqueGood.IS_PROMOTE)==1?true:false);
				good.setPromotePrice(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.PROMOTE_PRICE));
				good.setRankPrice(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.RANK_PRICE));
				good.setShopPrice(set.getString(IFuLiCenterBiz.NewAndBoutiqueGood.SHOP_PRICE));
				goods=Utils.add(goods, good);
			}
			return goods;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public CategoryParentBean[] findCategoryParent() {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.CategoryParent.TABLE_NAME;
		try {
			statement=connection.prepareStatement(sql);
			set=statement.executeQuery();
			CategoryParentBean[] parents=new CategoryParentBean[0];
			while(set.next()){
				CategoryParentBean parent=new CategoryParentBean();
				parent.setId(set.getInt(IFuLiCenterBiz.CategoryParent.ID));
				parent.setImageUrl(set.getString(IFuLiCenterBiz.CategoryParent.IMAGE_URL));
				parent.setName(set.getString(IFuLiCenterBiz.CategoryParent.NAME));
				parents=Utils.add(parents, parent);
			}
			return parents;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public CategoryChildBean[] findCategoryChildren(int parentId, int pageId,
			int pageSize) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.CategoryChild.TABLE_NAME
			+" where "+IFuLiCenterBiz.CategoryChild.PARENT_ID+"=?"
			+" limit ?,?";
		try {
			System.out.println(sql);
			statement=connection.prepareStatement(sql);
			statement.setInt(1, parentId);
			statement.setInt(2, pageId);
			statement.setInt(3,pageSize);
			set=statement.executeQuery();
			CategoryChildBean[] children=new CategoryChildBean[0];
			while(set.next()){
				CategoryChildBean child=new CategoryChildBean();
				child.setId(set.getInt(IFuLiCenterBiz.CategoryChild.ID));
				child.setImageUrl(set.getString(IFuLiCenterBiz.CategoryChild.IMAGE_URL));
				child.setName(set.getString(IFuLiCenterBiz.CategoryChild.NAME));
				child.setParentId(set.getInt(IFuLiCenterBiz.CategoryChild.PARENT_ID));
				children=Utils.add(children, child);
			}
			return children;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public GoodDetailsBean findGoodDetails(int goodsId) {
		GoodDetailsBean good=null;
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.GoodDetails.TABLE_NAME
			+" where "+IFuLiCenterBiz.GoodDetails.GOODS_ID+"=?";
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, goodsId);
			set=statement.executeQuery();
			if(set.next()){
				good = readGoodDetails(set);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}		
		if(good==null){
			return null;
		}
		PropertyBean[] properties=findProperties(good.getGoodsId());
//		ArrayList<PropertyBean> list = Utils.array2List(properties);
		if(properties!=null){
			good.setProperties(properties);
			for (PropertyBean p : properties) {
				AlbumBean[] albums=findAlbums(good.getGoodsId());
				if(albums!=null){
//					ArrayList<AlbumBean> albumList = Utils.array2List(albums);
					p.setAlbums(albums);
				}
			}
		}
		return good;
	}

	synchronized private AlbumBean[] findAlbums(int goodsId) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.Album.TABLE_NAME
			+" where "+IFuLiCenterBiz.Album.PID+"=?";
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, goodsId);
			set=statement.executeQuery();
			AlbumBean[] albums=new AlbumBean[0];
			while(set.next()){
				AlbumBean album=new AlbumBean();
				album.setPid(set.getInt(IFuLiCenterBiz.Album.PID));
				album.setImgId(set.getInt(IFuLiCenterBiz.Album.IMG_ID));
				album.setImgUrl(set.getString(IFuLiCenterBiz.Album.IMG_URL));
				album.setThumbUrl(set.getString(IFuLiCenterBiz.Album.THUMB_URL));
				albums=Utils.add(albums, album);
			}
			return albums;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	/**
	 * 查询指定商品id的属性
	 * @param goodId
	 * @return
	 */
	synchronized private PropertyBean[] findProperties(int goodsId) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.Property.TABLE_NAME
			+" where "+IFuLiCenterBiz.Property.GOODS_ID+"=?";
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, goodsId);
			set=statement.executeQuery();
			PropertyBean[] properties=new PropertyBean[0];
			while(set.next()){
				PropertyBean p=new PropertyBean();
				p.setId(set.getInt(IFuLiCenterBiz.Property.ID));
				p.setColorCode(set.getString(IFuLiCenterBiz.Property.COLOR_CODE));
				p.setColorId(set.getInt(IFuLiCenterBiz.Property.COLOR_ID));
				p.setColorImg(set.getString(IFuLiCenterBiz.Property.COLOR_IMG));
				p.setColorName(set.getString(IFuLiCenterBiz.Property.COLOR_NAME));
				p.setColorUrl(set.getString(IFuLiCenterBiz.Property.COLOR_URL));
				properties=Utils.add(properties, p);
			}
			return properties;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public ColorBean[] findColorsByCatId(int catId) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.Color.TABLE_NAME
			+" where "+IFuLiCenterBiz.Color.CAT_ID+"=?";
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, catId);
			set=statement.executeQuery();
			ColorBean[] colors=new ColorBean[0];
			while(set.next()){
				ColorBean color=new ColorBean();
				color.setCatId(catId);
				color.setColorCode(set.getString(IFuLiCenterBiz.Color.COLOR_CODE));
				color.setColorId(set.getInt(IFuLiCenterBiz.Color.COLOR_ID));
				color.setColorImg(set.getString(IFuLiCenterBiz.Color.COLOR_IMG));
				color.setColorName(set.getString(IFuLiCenterBiz.Color.COLOR_NAME));
				colors=Utils.add(colors, color);
			}
			return colors;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public int addCollect(CollectBean collect) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + IFuLiCenterBiz.Collect.TABLE_NAME + "("
				+ IFuLiCenterBiz.Collect.GOODS_ID + ","
				+ IFuLiCenterBiz.Collect.USER_NAME + ","
				+ IFuLiCenterBiz.Collect.GOODS_NAME + ","
				+ IFuLiCenterBiz.Collect.GOODS_ENGLISH_NAME+ ","
				+ IFuLiCenterBiz.Collect.GOODS_THUMB+ ","
				+ IFuLiCenterBiz.Collect.GOODS_IMG+ ","
				+ IFuLiCenterBiz.Collect.ADD_TIME
				+ ")values(?,?,?,?,?,?,?)";
		int count =0;
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, collect.getGoodsId());
			statement.setString(2, collect.getUserName());
			statement.setString(3, collect.getGoodsName());
			statement.setString(4, collect.getGoodsEnglishName());
			statement.setString(5, collect.getGoodsThumb());
			statement.setString(6, collect.getGoodsImg());
			statement.setLong(7, collect.getAddTime());
			count = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		if(count==1){
			int id=findCollecLastId();
			return id;
		}
		return 0;
	}

	/**
	 * 查询刚插入衣橱的记录的id
	 * @return
	 */
	synchronized private int findCollecLastId() {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select id from " + IFuLiCenterBiz.Collect.TABLE_NAME
			+" order by id desc limit 0,1";
		try {
			statement = connection.prepareStatement(sql);
			set = statement.executeQuery();
			if (set.next()) {
				int id=set.getInt(IFuLiCenterBiz.Collect.ID);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}
		return 0;
	}

	@Override
	synchronized public boolean deleteCollect(String userName,int id) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "delete from " + IFuLiCenterBiz.Collect.TABLE_NAME
			+ " where " + IFuLiCenterBiz.Collect.GOODS_ID + "=? and "
			+ IFuLiCenterBiz.Collect.USER_NAME + "=?";
//			+I.User.USER_NAME+"=?";
		System.out.println("deleteCollect,sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, userName);
			int count = statement.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}
	
	@Override
	synchronized public CollectBean[] findCollects(String userName, int pageId, int pageSize) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + IFuLiCenterBiz.Collect.TABLE_NAME
				+ " where " + IFuLiCenterBiz.Collect.USER_NAME + "=? limit ?,?";
		CollectBean[] collects=new CollectBean[0];
		try {
			CollectBean w=new CollectBean();
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set = statement.executeQuery();
			while (set.next()) {
				w=readCollect(set);
				collects=Utils.add(collects, w);
			}
			return collects;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}
	

	@Override
	synchronized public int findCollectCount(String userName) {
		System.out.println("findCollectCount,userName="+userName);
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select count(*) as count from " + IFuLiCenterBiz.Collect.TABLE_NAME
				+ " where " + IFuLiCenterBiz.Collect.USER_NAME + "=?";
		try {
//			CollectBean w=new CollectBean();
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			set = statement.executeQuery();
			if (set.next()) {
				int count=set.getInt(IFuLiCenterBiz.Collect.COUNT);
				return count;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}
		return 0;
	}

	/**
	 * 读取一条收藏的商品的记录
	 * @param set
	 * @return
	 */
	synchronized private CollectBean readCollect(ResultSet set) {
		CollectBean collect=new CollectBean();
		try {
			collect.setId(set.getInt(IFuLiCenterBiz.Collect.ID));
			collect.setAddTime(set.getLong(IFuLiCenterBiz.Collect.ADD_TIME));
			collect.setGoodsEnglishName(set.getString(IFuLiCenterBiz.Collect.GOODS_ENGLISH_NAME));
			collect.setGoodsId(set.getInt(IFuLiCenterBiz.Collect.GOODS_ID));
			collect.setGoodsImg(set.getString(IFuLiCenterBiz.Collect.GOODS_IMG));
			collect.setGoodsName(set.getString(IFuLiCenterBiz.Collect.GOODS_NAME));
			collect.setGoodsThumb(set.getString(IFuLiCenterBiz.Collect.GOODS_THUMB));
			collect.setUserName(set.getString(IFuLiCenterBiz.Collect.USER_NAME));
			return collect;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	synchronized public boolean isCollect(String userName, int goodsId) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select id from " + IFuLiCenterBiz.Collect.TABLE_NAME
			+" where "+IFuLiCenterBiz.Collect.USER_NAME+"=? and "
			+IFuLiCenterBiz.Collect.GOODS_ID+"=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setInt(2, goodsId);
			set = statement.executeQuery();
			if (set.next()) {
				int id=set.getInt(IFuLiCenterBiz.Collect.ID);
				return id>0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}
		return false;
	}
	
	@Override
	synchronized public ArrayList<GoodDetailsBean> findGoodsDetails(int catId, int pageId, int pageSize) {
		ArrayList<GoodDetailsBean> goodDetailsList=new ArrayList<GoodDetailsBean>();
		GoodDetailsBean good=null;
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from "+IFuLiCenterBiz.GoodDetails.TABLE_NAME
			+" where "+IFuLiCenterBiz.GoodDetails.CAT_ID+"=?";
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, catId);
			set=statement.executeQuery();
			while(set.next()){
				good = readGoodDetails(set);
				goodDetailsList.add(good);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}		
		if(goodDetailsList==null){
			return null;
		}
		for (GoodDetailsBean goodDetails : goodDetailsList) {
			PropertyBean[] properties=findProperties(good.getGoodsId());
//			ArrayList<PropertyBean> list = Utils.array2List(properties);
			if(properties!=null){
				goodDetails.setProperties(properties);
				for (PropertyBean p : properties) {
					AlbumBean[] albums=findAlbums(good.getGoodsId());
					if(albums!=null){
						p.setAlbums(albums);
					}
				}
			}
		}
		return goodDetailsList;
	}

	private GoodDetailsBean readGoodDetails(ResultSet set) throws SQLException {
		GoodDetailsBean good;
		good=new GoodDetailsBean();
		good.setAddTime(set.getLong(IFuLiCenterBiz.GoodDetails.ADD_TIME));
		good.setCatId(set.getInt(IFuLiCenterBiz.GoodDetails.CAT_ID));
		good.setCurrencyPrice(set.getString(IFuLiCenterBiz.GoodDetails.CURRENT_PRICE));
		good.setGoodsId(set.getInt(IFuLiCenterBiz.GoodDetails.GOODS_ID));
		good.setGoodsBrief(set.getString(IFuLiCenterBiz.GoodDetails.GOODS_BRIEF));
		good.setGoodsEnglishName(set.getString(IFuLiCenterBiz.GoodDetails.GOODS_ENGLISH_NAME));
		good.setGoodsImg(set.getString(IFuLiCenterBiz.GoodDetails.GOODS_IMG));
		good.setGoodsName(set.getString(IFuLiCenterBiz.GoodDetails.GOODS_NAME));
		good.setGoodsThumb(set.getString(IFuLiCenterBiz.GoodDetails.GOODS_THUMB));
		good.setId(set.getInt(IFuLiCenterBiz.GoodDetails.ID));
		good.setPromote(set.getInt(IFuLiCenterBiz.GoodDetails.IS_PROMOTE)==1?true:false);
		good.setPromotePrice(set.getString(IFuLiCenterBiz.GoodDetails.PROMOTE_PRICE));
		good.setRankPrice(set.getString(IFuLiCenterBiz.GoodDetails.RANK_PRICE));
		good.setShopPrice(set.getString(IFuLiCenterBiz.GoodDetails.SHOP_PRICE));
		good.setShareUrl(set.getString(IFuLiCenterBiz.GoodDetails.SHARE_URL));
		return good;
	}
	
	private final String SQL_QUERY_AVATAR = ","+ I.Avatar.TABLE_NAME;
	private final String SQL_QUERY_USER = ","+ I.User.TABLE_NAME;
	private final String SQL_COMPARE_USER_NAME_AVATAR = " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " ";
	private final String SQL_COMPARE_USER_ID_AVATAR = " and " + I.User.USER_ID + "=" + I.Avatar.USER_ID + " ";
//	private final String SQL_COMPARE_USER_NAME_LOCATION = " and " + I.User.USER_NAME + "=" + I.Location.USER_NAME + " ";
//	private final String SQL_COMPARE_USER_NAME_CONTACT = " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " ";
	private final String SQL_COMPARE_USER_ID_CONTACT = " and " + I.User.USER_ID + "=" + I.Contact.CU_ID + " ";
	private final String SQL_COMPARE_AVATAR_USER = " and " + I.Avatar.AVATAR_TYPE + "=0 ";

	@Override
	synchronized public User findUserByUserName(String userName) {
		System.out.println("SuperQQDao.findUserByUserName,userName="+userName);
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME 
				+ SQL_QUERY_AVATAR
				+ " where " + I.User.USER_NAME + "=?" 
				+ SQL_COMPARE_USER_NAME_AVATAR
				+ SQL_COMPARE_AVATAR_USER;
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			set = statement.executeQuery();
			if(set.next()){
				User user = new User();
				readUser(set, user);
				readAvatar(set, user);
				System.out.println("user="+userName.toString());
				return user;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}
	
	@Override
	synchronized public User findUserById(int id) {
		System.out.println("SuperQQDao.findUserById,id="+id);
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME 
				+ SQL_QUERY_AVATAR
				+ " where " + I.User.USER_ID + "=?" 
				+ SQL_COMPARE_USER_NAME_AVATAR
				+ SQL_COMPARE_AVATAR_USER;
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			set = statement.executeQuery();
			if(set.next()){
				User user = new User();
				readUser(set, user);
				readAvatar(set, user);
				System.out.println("id="+id);
				return user;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}
	/**
	 * 从set中读取user表的一条记录
	 * @param set
	 * @return
	 * @throws SQLException
	 */
	private void readUser(ResultSet set,User user) throws SQLException {
		user.setMUserId(set.getInt(I.User.USER_ID));
		user.setMUserName(set.getString(I.User.USER_NAME));
		user.setMUserPassword(set.getString(I.User.PASSWORD));
		user.setMUserNick(set.getString(I.User.NICK));
		user.setMUserUnreadMsgCount(set.getInt(I.User.UN_READ_MSG_COUNT));
	}
	
	private void readAvatar(ResultSet set, Avatar avatar) throws SQLException {
		avatar.setMAvatarId(set.getInt(I.Avatar.AVATAR_ID));
		avatar.setMAvatarUserId(set.getInt(I.Avatar.USER_ID));
		avatar.setMAvatarUserName(set.getString(I.Avatar.USER_NAME));
		avatar.setMAvatarPath(set.getString(I.Avatar.AVATAR_PATH));
		avatar.setMAvatarType(set.getInt(I.Avatar.AVATAR_TYPE));
	}
	
	@Override
	synchronized public User[] findUsersByUserName(String userName, int pageId, int pageSize) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME 
				+ SQL_QUERY_AVATAR
				+ " where " + I.User.USER_NAME + " like ?" 
				+ SQL_COMPARE_USER_NAME_AVATAR
				+ SQL_COMPARE_AVATAR_USER
				+ " limit ?,?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, "%"+userName+"%");
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set = statement.executeQuery();
			User[] users = new User[0];
			while(set.next()){
				User user = new User();
				readUser(set, user);
				readAvatar(set, user);
				users = Utils.add(users, user);
			}
			return users;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	synchronized public User[] findUsersByNick(String nick, int pageId, int pageSize) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME 
				+ SQL_QUERY_AVATAR
				+ " where " + I.User.NICK + " like ?" 
				+ SQL_COMPARE_USER_NAME_AVATAR
				+ SQL_COMPARE_AVATAR_USER
				+ " limit ?,?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, "%"+nick+"%");
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set = statement.executeQuery();
			User[] users = new User[0];
			while(set.next()){
				User user = new User();
				readUser(set, user);
				readAvatar(set, user);
				users = Utils.add(users, user);
			}
			return users;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	synchronized public User[] findUsersForSearch(String nick,String username, int pageId, int pageSize) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME 
				+ SQL_QUERY_AVATAR
				+ " where " + I.User.NICK + " like ?" 
				+ " or " + I.User.USER_NAME + " like ?"
				+ SQL_COMPARE_USER_NAME_AVATAR
				+ SQL_COMPARE_AVATAR_USER
				+ " limit ?,?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, "%"+nick+"%");
			statement.setString(2, "%"+username+"%");
			statement.setInt(3, pageId);
			statement.setInt(4, pageSize);
			set = statement.executeQuery();
			User[] users = new User[0];
			while(set.next()){
				User user = new User();
				readUser(set, user);
				readAvatar(set, user);
				users = Utils.add(users, user);
			}
			return users;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	synchronized public int addUser(User user) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + I.User.TABLE_NAME
				+ "(" + I.User.USER_NAME
				+ "," + I.User.PASSWORD 
				+ "," + I.User.NICK
				+ "," + I.User.UN_READ_MSG_COUNT 
				+ ")values(?,?,?,?)";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, user.getMUserName());
			statement.setString(2, user.getMUserPassword());
			statement.setString(3, user.getMUserNick());
			statement.setInt(4, user.getMUserUnreadMsgCount());
			statement.executeUpdate();
			set = statement.getGeneratedKeys();
			if(set!=null && set.next()){
				int id = set.getInt(1);
				user.setMUserId(id);
				return id;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return 0;
	}

	@Override
	synchronized public boolean deleteUser(String userName) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "delete from " + I.User.TABLE_NAME
				+ " where " + I.User.USER_NAME + "=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			int count = statement.executeUpdate();
			return count>0;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	synchronized public boolean updateUserNick(User user, String newNick) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.User.TABLE_NAME
				+ " set "+ I.User.NICK + "=?"
				+ " where "+I.User.USER_NAME+"=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, newNick);
			statement.setString(2, user.getMUserName());
			int count = statement.executeUpdate();
			return count == 1;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	synchronized public boolean updateUserPassword(User user, String newPassword) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.User.TABLE_NAME
				+ " set "+ I.User.PASSWORD + "=?"
				+ " where "+I.User.USER_NAME+"=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, newPassword);
			statement.setString(2, user.getMUserName());
			int count = statement.executeUpdate();
			return count == 1;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}
	
	@Override
	synchronized public boolean isExistsContact(String userName, String name) {
		PreparedStatement statement = null;
		ResultSet set = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select " + I.Contact.CONTACT_ID
				+ " from " + I.Contact.TABLE_NAME
				+ " where " + I.Contact.USER_NAME + "=?"
				+ " and " + I.Contact.CU_NAME + "=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setString(2, name);
			set = statement.executeQuery();
			if(set.next()){
				int id = set.getInt(I.Contact.CONTACT_ID);
				return id>0;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return false;
	}

	/**
	 * 根据账号查询id
	 * @param userName
	 * @return
	 */
	synchronized private int findIdByUserName(String userName){
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select " + I.User.USER_ID
				+ " from " + I.User.TABLE_NAME
				+ " where " + I.User.USER_NAME + "=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			set = statement.executeQuery();
			if(set.next()){
				int id = set.getInt(I.User.USER_ID);
				return id;
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return 0;
	}

	@Override
	synchronized public Contact findContactById(int myuid, int cuid) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME
				+ SQL_QUERY_USER + SQL_QUERY_AVATAR
				+ " where " + I.Contact.USER_ID + "=? and "
				+ I.Contact.CU_ID + "=?" 
				+ SQL_COMPARE_USER_ID_CONTACT
				+ SQL_COMPARE_USER_ID_AVATAR
				+ SQL_COMPARE_AVATAR_USER;
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, myuid);
			statement.setInt(2, cuid);
			set = statement.executeQuery();
			if(set.next()){
				Contact contact = new Contact();
				readContact(set, contact);
				readUser(set, contact);
				readAvatar(set, contact);
				return contact;
			}
		}catch (SQLException e){
			e.printStackTrace();
		} finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	/**
	 * 从set中获取contact表中的一条记录
	 * @param set
	 * @return
	 * @throws SQLException 
	 */
	private void readContact(ResultSet set,Contact contact) throws SQLException {
		contact.setMContactId(set.getInt(I.Contact.CONTACT_ID));
		contact.setMContactUserId(set.getInt(I.Contact.USER_ID));
		contact.setMContactUserName(set.getString(I.Contact.USER_NAME));
		contact.setMContactCid(set.getInt(I.Contact.CU_ID));
		contact.setMContactCname(set.getString(I.Contact.CU_NAME));
	}

	@Override
	synchronized public Contact findContactById(int id) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from " + I.Contact.TABLE_NAME
				+ SQL_QUERY_USER + SQL_QUERY_AVATAR
				+ " where "+I.Contact.CONTACT_ID+"=?"
				+ SQL_COMPARE_USER_ID_CONTACT
				+ SQL_COMPARE_USER_ID_AVATAR
				+ SQL_COMPARE_AVATAR_USER;
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, id);
			set=statement.executeQuery();
			if(set.next()){
				Contact c = new Contact();
				readContact(set, c);
				readUser(set, c);
				readAvatar(set, c);
				return c;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}
	@Override
	synchronized public Contact[] findContactsByUserName(String userName) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from " + I.Contact.TABLE_NAME
				+ SQL_QUERY_USER + SQL_QUERY_AVATAR
				+ " where "+I.Contact.USER_NAME+"=?"
				+ SQL_COMPARE_USER_ID_CONTACT
				+ SQL_COMPARE_USER_ID_AVATAR
				+ SQL_COMPARE_AVATAR_USER;
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setString(1, userName);
			set=statement.executeQuery();
			Contact[] contacts=new Contact[0];
			while(set.next()){
				Contact c = new Contact();
				readContact(set, c);
				readUser(set, c);
				readAvatar(set, c);
				contacts=Utils.add(contacts, c);
			}
			return contacts;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public Contact[] findContactListByMyuid(int myuid) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from " + I.Contact.TABLE_NAME
				+ SQL_QUERY_USER + SQL_QUERY_AVATAR
				+ " where "+I.Contact.USER_ID+"=?"
				+ SQL_COMPARE_USER_ID_CONTACT
				+ SQL_COMPARE_USER_ID_AVATAR
				+ SQL_COMPARE_AVATAR_USER;
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, myuid);
			set=statement.executeQuery();
			Contact[] contacts=new Contact[0];
			while(set.next()){
				Contact c = new Contact();
				readContact(set, c);
				readUser(set, c);
				readAvatar(set, c);
				contacts=Utils.add(contacts, c);
			}
			return contacts;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		
		return null;
	}

	@Override
	synchronized public Contact[] findContactsByUserName(String userName, int pageId, int pageSize) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from " + I.Contact.TABLE_NAME
				+ SQL_QUERY_USER + SQL_QUERY_AVATAR
				+ " where "+I.Contact.USER_NAME+"=?"
				+ SQL_COMPARE_USER_ID_CONTACT
				+ SQL_COMPARE_USER_ID_AVATAR
				+ SQL_COMPARE_AVATAR_USER
				+ " limit ?,?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set=statement.executeQuery();
			Contact[] contacts=new Contact[0];
			while(set.next()){
				Contact c = new Contact();
				readContact(set, c);
				readUser(set, c);
				readAvatar(set, c);
				contacts=Utils.add(contacts, c);
			}
			return contacts;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public Contact[] findContactListByMyuid(int myuid, int pageId, int pageSize) {
		ResultSet set=null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="select * from " + I.Contact.TABLE_NAME
				+ SQL_QUERY_USER + SQL_QUERY_AVATAR
				+ " where "+I.Contact.USER_ID+"=?"
				+ SQL_COMPARE_USER_ID_CONTACT
				+ SQL_COMPARE_USER_ID_AVATAR
				+ SQL_COMPARE_AVATAR_USER
				+ " limit ?,?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, myuid);
			statement.setInt(2, pageId);
			statement.setInt(3, pageSize);
			set=statement.executeQuery();
			Contact[] contacts=new Contact[0];
			while(set.next()){
				Contact c = new Contact();
				readContact(set, c);
				readUser(set, c);
				readAvatar(set, c);
				contacts=Utils.add(contacts, c);
			}
			return contacts;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(set, statement, connection);
		}
		
		return null;
	}
	@Override
	synchronized public Contact addContact(String userName, String name) {
		boolean existsContact = isExistsContact(userName, name);
		Contact contact = new Contact();
		if(existsContact){
			System.out.println("已经是联系人");
			contact.setResult(false);
			contact.setMsg(I.MSG_CONTACT_FIRENDED);
			return contact;
		}
		int myuid=findIdByUserName(userName);
		int cuid=findIdByUserName(name);
		contact.setResult(false);
		contact.setMsg(I.MSG_CONTACT_FAIL);
		ResultSet set = null;
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="insert into "+I.Contact.TABLE_NAME
			+"("+I.Contact.USER_ID
			+","+I.Contact.USER_NAME
			+","+I.Contact.CU_ID
			+","+I.Contact.CU_NAME
			+")values(?,?,?,?)";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, myuid);
			statement.setString(2, userName);
			statement.setInt(3, cuid);
			statement.setString(4, name);
			statement.executeUpdate();
			set = statement.getGeneratedKeys();
			if(set!=null && set.next()){
				int id = set.getInt(1);
				if(id>0){
					contact = findContactById(id);
					contact.setResult(true);
				}
			}			
			return contact;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return null;
	}

	@Override
	synchronized public boolean deleteContact(int myuid, int cuid) {
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="delete from "+I.Contact.TABLE_NAME
			+" where "+I.Contact.USER_ID+"=? and "
			+I.Contact.CU_ID+"=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, myuid);
			statement.setInt(2, cuid);
			int count = statement.executeUpdate();
			return count>0;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	synchronized public boolean deleteContact(String userName, String name) {
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql="delete from "+I.Contact.TABLE_NAME
			+" where "+I.Contact.USER_NAME+"=? and "
			+I.Contact.CU_NAME+"=?";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setString(2, name);
			int count = statement.executeUpdate();
			return count>0;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}
	
	@Override
	synchronized public boolean updateAvatar(int id, String name, String path, int type) {
		PreparedStatement statement=null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + I.Avatar.TABLE_NAME
				+ "(" + I.Avatar.USER_ID
				+ "," + I.Avatar.USER_NAME 
				+ "," + I.Avatar.AVATAR_PATH
				+ "," + I.Avatar.AVATAR_TYPE + ")values(?,?,?,?)";
		System.out.println("connection="+connection+",sql="+sql);
		try {
			statement=connection.prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, name);
			statement.setString(3, path);
			statement.setInt(4, type);
			int count = statement.executeUpdate();
			return count>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils.closeAll(null, statement, connection);
		}
			
		return false;
	}
	

}
