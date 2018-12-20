package org.shopping.pojogroup;

import java.io.Serializable;
import java.util.List;

import org.shopping.pojo.TbSpecification;
import org.shopping.pojo.TbSpecificationOption;

/**
 * 新增规格及规格详细实体类
 * 
 * @author root
 *
 */
public class Specification implements Serializable {
	private static final long serialVersionUID = 1L;
	private TbSpecification specification;// 规格信息
	private List<TbSpecificationOption> specificationOptionList;// 规格属性信息

	public TbSpecification getSpecification() {
		return specification;
	}

	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}

	public List<TbSpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}

	public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}

}
