package Base;

import java.util.List;
import entity.PageResult;

public interface BaseService<T> {
	 public T queryById(Long id);
	 public List<T> queryAll();
	 public boolean deleteByIds(Class<T> clazz,List<Object> values);
	 public boolean delete(Long[] ids);
	 public Integer deleteById(Long id);
	 public Integer updateSelective(T param);
	 public Integer update(T param);
	 public Integer saveSelect(T param);
	 public Integer save(T param);
	 public T queryOne(T param);
	 public PageResult queryPageListByWhere(T param,Integer page,Integer rows);
	 public Integer queryCount(T param);
	 public List<T> queryListByWhere(T param);
	 public PageResult findPage(T bean, int pageNum, int pageSize);
}
