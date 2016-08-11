package cn.ucai.fulicenter.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
/**
 * 购物车
 * 
 * @author yao
 *
 */
public class CartBean implements Serializable {
	private  int id;
	private String userName;

	private int goodsId;
	/** 购物车中的商品信息 */
	/** 该商品被选中的件数 */
	private int count;
	@JsonProperty("isChecked")
	private boolean isChecked;

	private String goods;

	public CartBean(int id, String userName, int goodsId, int count, boolean isChecked, String goods) {
		this.id = id;
		this.userName = userName;
		this.goodsId = goodsId;
		this.count = count;
		this.isChecked = isChecked;
		this.goods = goods;
	}


	public String getGoods() {
		return goods;
	}

	public void setGoods(GoodDetailsBean goods) {
		this.goods = String.valueOf(goods);
	}

	@JsonIgnore
	public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CartBean() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "CartBean{" +
				"id=" + id +
				", userName='" + userName + '\'' +
				", goodsId=" + goodsId +
				", count=" + count +
				", isChecked=" + isChecked +
				", goods='" + goods + '\'' +
				'}';
	}
}
