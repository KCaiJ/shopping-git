package org.sellergoods.service.impl;
/**
 * 规格管理
 */


import java.util.List;

import org.sellergoods.service.SpecificationService;
import org.shopping.mapper.TbSpecificationMapper;
import org.shopping.mapper.TbSpecificationOptionMapper;
import org.shopping.pojo.TbSpecification;
import org.shopping.pojo.TbSpecificationOption;
import org.shopping.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
@Service 
@Transactional
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService{
	@Autowired
	private TbSpecificationMapper SpecificationMapper;
	@Autowired
	private TbSpecificationOptionMapper SpecificationOptionMapper;
	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbSpecification bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Example example=new Example(TbSpecification.class);
		Criteria criteria = example.createCriteria();		
		if(bean!=null){
			if(bean.getSpecName()!=null && bean.getSpecName().length()>0){
				criteria.andLike("specName", "%"+bean.getSpecName()+"%");
			}
		}		
		Page<TbSpecification> page= (Page<TbSpecification>)SpecificationMapper.selectByExample(example);	
		return new PageResult(page.getTotal(), page.getResult());
	}

	//增加规格及规格详细
	@Transactional
	@Override
	public boolean save(Specification param) {
		try{
			SpecificationMapper.insertToId(param.getSpecification());
			for(TbSpecificationOption bean:param.getSpecificationOptionList()){
				bean.setSpecId(param.getSpecification().getId());
				SpecificationOptionMapper.insert(bean);
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚事务
            return false;
        }
	}

	@Override
	public Specification findOne(Long id) {
		//查询规格
		TbSpecification tbSpecification = SpecificationMapper.selectByPrimaryKey(id);
		//查询规格详细
		Example example=new Example(TbSpecificationOption.class);
		Criteria criteria=example.createCriteria();
		criteria.andEqualTo("specId", id);
		List<TbSpecificationOption> optionList=SpecificationOptionMapper.selectByExample(example);
		Specification spec=new Specification();
		spec.setSpecification(tbSpecification);
		spec.setSpecificationOptionList(optionList);	
		return spec;
	}
	
	@Transactional
	@Override
	public boolean delete(Long[] ids) {
		try{
			for(Long id:ids){
				SpecificationMapper.deleteByPrimaryKey(id);			
				//删除原有的规格选项		
				Example example=new Example(TbSpecificationOption.class);
				Criteria criteria=example.createCriteria();			
				criteria.andEqualTo("specId", id);
				SpecificationOptionMapper.deleteByExample(example);
			}
			return true;
		}catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚事务
            return false;
        }
	}

	//保存修改
	@Transactional
	@Override
	public boolean update(Specification specification) {
		try {
			//保存修改的规格
			SpecificationMapper.updateByPrimaryKey(specification.getSpecification());//保存规格
			//删除原有的规格选项		
			Example example=new Example(TbSpecificationOption.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("specId",specification.getSpecification().getId());//指定规格ID为条件
			SpecificationOptionMapper.deleteByExample(example);//删除		
			//循环插入规格选项
			for(TbSpecificationOption specificationOption:specification.getSpecificationOptionList()){			
				specificationOption.setSpecId(specification.getSpecification().getId());
				SpecificationOptionMapper.insert(specificationOption);		
			}	
			 return true;
		} catch (Exception e) {
			 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚事务
	         return false;
		}
	}
}
