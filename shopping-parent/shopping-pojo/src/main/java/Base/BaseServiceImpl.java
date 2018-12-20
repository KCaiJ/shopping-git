package Base;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import entity.PageResult;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

public abstract class BaseServiceImpl<T>  {
    
    @Autowired
    private Mapper<T> mapper;
       
    //根据id查询实体
    public T queryById(Long id){
        return this.mapper.selectByPrimaryKey(id);
    }
    
    //查询所有
    public List<T> queryAll(){
        return this.mapper.select(null);
    }
    
    //条件查询
    public List<T> queryListByWhere(T param){
        return this.mapper.select(param);
    }
    
    //查询记录数
    public Integer queryCount(T param){
        return this.mapper.selectCount(param);
    }
    
    //分页
    public PageResult queryPageListByWhere(T param,Integer page,Integer rows){
        PageHelper.startPage(page, rows);
        Page<T> list=(Page<T>)this.queryListByWhere(param);
        return new PageResult(list.getTotal(), list.getResult());
    }
       
    //查询一条记录
    public T queryOne(T param){
        return this.mapper.selectOne(param);
    }
    
    //插入
    public Integer save(T param){
        return this.mapper.insert(param);
    }
    
    //新增非空字段
    public Integer saveSelect(T param){
        return this.mapper.insertSelective(param);
    }
    
    //根据主键更新
    public Integer update(T param){
        return this.mapper.updateByPrimaryKey(param);
    }
    
    //根据主键更新非空字段
    public Integer updateSelective(T param){
        return this.mapper.updateByPrimaryKeySelective(param);
    }
       
    //根据主键删除
    public Integer deleteById(Long id){
        return this.mapper.deleteByPrimaryKey(id);
    }  
    
  //批量删除
    @Transactional
	public boolean delete(Long[] ids) {
		try{
			for(Long id:ids){
				this.mapper.deleteByPrimaryKey(id);
			}	
			return true;
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚事务
            return false;
        }
	}
    
    
    
    @Transactional
    public boolean deleteByIds(Class<T> clazz,List<Object> values){
    	try{
    		Example example = new Example(clazz);
	        example.createCriteria().andIn("id", values);
	        return true;
    	}catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚事务
            return false;
        }
    }  
    
}