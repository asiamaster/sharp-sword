package com.dili.utils.beetl;

import java.util.ArrayList;
import java.util.List;

import org.beetl.core.Tag;

public class TagChildrenContext {
		private List<HTMLTag> children = null;

	public List<HTMLTag> getChildren() {
		if(children==null) children = new ArrayList<HTMLTag>();
		return children;
	}
	public void setChildren(List<HTMLTag> children) {
		this.children = children;
	}
	
}
