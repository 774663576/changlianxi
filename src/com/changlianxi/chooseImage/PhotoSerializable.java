package com.changlianxi.chooseImage;

import java.io.Serializable;
import java.util.List;


/**    
 */
public class PhotoSerializable implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<PhotoInfo> list;

	public List<PhotoInfo> getList() {
		return list;
	}

	public void setList(List<PhotoInfo> list) {
		this.list = list;
	}
	
}
