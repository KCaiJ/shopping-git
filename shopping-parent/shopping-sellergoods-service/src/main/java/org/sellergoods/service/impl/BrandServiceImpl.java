package org.sellergoods.service.impl;
/**
 * 品牌管理
 */

import org.sellergoods.service.BrandService;
import org.shopping.mapper.TbBrandMapper;
import org.shopping.pojo.TbBrand;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
@Service 
public class BrandServiceImpl  extends  BaseServiceImpl<TbBrand>  implements BrandService{
	@Autowired
	private TbBrandMapper BrandMapper;
	
	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbBrand bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Example example=new Example(TbBrand.class);
		Criteria criteria = example.createCriteria();		
		if(bean!=null){
			if(bean.getName()!=null && bean.getName().length()>0){
				criteria.andLike("name", "%"+bean.getName()+"%");
			}
			if(bean.getFirstChar()!=null && bean.getFirstChar().length()>0){
				criteria.andEqualTo("firstChar",bean.getFirstChar());
			}		
		}		
		Page<TbBrand> page= (Page<TbBrand>)BrandMapper.selectByExample(example);	
		return new PageResult(page.getTotal(), page.getResult());
	}
}
