package com.isteer.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isteer.entity.Departments;
import com.isteer.entity.Employee;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.DepartmentIdNullException;
import com.isteer.exception.TenantIdNullException;
import com.isteer.repository.dao.DeapartmentRepoDao;
import com.isteer.util.DepartmentRowMapper;
import com.isteer.util.EmployeeRowMapper;

@Component
public class DepartmentRepoDaoImpl implements DeapartmentRepoDao{
	
	@Autowired
	NamedParameterJdbcTemplate template;

			
	 // Method to check if the tenant exists by explicitly selecting columns
	@Transactional
	@Override
    public boolean isTenantExist(String tenantId) {
        String checkTenantExistsQuery = "SELECT tenant_uuid FROM tenants WHERE tenant_uuid = :tenantId AND tenant_status = :status";
        
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("tenantId", tenantId);
        param.addValue("status", 1);  // Assuming '1' indicates active tenants

        List<String> tenantUuids = template.queryForList(checkTenantExistsQuery, param, String.class);

        return !tenantUuids.isEmpty(); // Return true if tenant exists
    }
	
	@Transactional
 // Method to check if the department exists
    @Override
    public boolean isDepartmentExist(String departmentName, String tenantId) {
        String checkDepartmentExistsQuery = "SELECT department_name FROM departments "
                + "WHERE department_name = :departmentName AND tenant_id = :tenantId AND department_status = :status";
        
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("departmentName", departmentName);
        param.addValue("tenantId", tenantId);
        param.addValue("status", 1);  // Assuming '1' indicates active departments
        
        List<String> departmentNames = template.queryForList(checkDepartmentExistsQuery, param, String.class);
        return !departmentNames.isEmpty();  // Returns true if the department exists
    }

    @Transactional
    // Method to add the department (also not using * in SQL)
    @Override
    public int addDepartment(String tenantId , Departments department) {
    	UUID uuid = UUID.randomUUID();
        String departmentUuid = uuid.toString();
        UUID uuid1 = UUID.randomUUID();
        String hodId = uuid1.toString();
            
        
    	String addDepartmentQuery = "INSERT INTO departments (department_uuid, department_head_uuid, tenant_id, department_name, contact_email, contact_phone, description) "
                + "VALUES (:departmentUuid, :headId, :tenantId, :departmentName, :email, :phone, :description)";

        SqlParameterSource params = new MapSqlParameterSource()
        		.addValue("departmentUuid", departmentUuid)
        		.addValue("headId", hodId)
                .addValue("tenantId", department.getTenantUuid())
                .addValue("departmentName", department.getDepartmentName())
                .addValue("email", department.getEmail())
                .addValue("phone", department.getPhoneNumber())
                .addValue("description", department.getDescription());

        return template.update(addDepartmentQuery, params);
    }
    
   @Transactional
    public Optional<Departments> findbyId(String departmentId){
    	String sql = "SELECT department_uuid, department_head_uuid, tenant_id, department_name, contact_email, contact_phone, description FROM departments where department_uuid = :departmentId";
    	SqlParameterSource param = new MapSqlParameterSource()
    			.addValue("departmentId", departmentId);
    	 try {
             Departments WrkDepartments = template.queryForObject(sql, param, new DepartmentRowMapper());
             return Optional.ofNullable(WrkDepartments);
         } catch (EmptyResultDataAccessException e) {
             // Return an empty Optional if no tenant found
             return Optional.empty();
         }
    	
    }
   @Transactional
	@Override
	public int updateDepartment(Departments department) {
		
		Optional<Departments> existingDepartment = findbyId(department.getDepartmentUuid());
		
		if(!existingDepartment.isPresent()) {
			return -1;
		}
		
		String sql = "UPDATE departments SET department_name = :departmentName, contact_email = :email, contact_phone = :phoneNumber, description = :description WHERE department_uuid = :departmentId";
		
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("departmentId", department.getDepartmentUuid())
				.addValue("departmentName", department.getDepartmentName())
				.addValue("email", department.getEmail())
				.addValue("phoneNumber", department.getPhoneNumber())
				.addValue("description", department.getDescription());
		  try {
	            // Execute the update query
	            return template.update(sql, param);
	        } catch (DataAccessException e) {
	                 e.printStackTrace();

	            return 0;  // Indicating failure to update
	        }
	    }
	
	@Transactional
	@Override
	public List<Departments> getAllDepartmentsByTenants(String tenantUuid){
		 // SQL query to fetch departments for a specific tenant where department status is active
	    String sql = "SELECT d.department_uuid, d.department_head_uuid, d.tenant_id, d.department_name, " +
	                 "d.contact_email, d.contact_phone, d.description " +
	                 "FROM departments d " +
	                 "JOIN tenants t ON d.tenant_id = t.tenant_uuid " +
	                 "WHERE d.department_status = :status AND t.tenant_status = 1 " +
	                 "AND d.tenant_id = :tenantId";

	    // Parameters to inject into the query
	    SqlParameterSource param = new MapSqlParameterSource()
	                               .addValue("status", 1) // Only active departments
	                               .addValue("tenantId", tenantUuid);

	    // Execute the query and map the result to Department objects
	    return template.query(sql, param, new DepartmentRowMapper());

	}
	
	@Transactional
	// Fetch all departments from all tenants
	public List<Departments> getAllDepartments() {
	    String sql = "SELECT d.department_uuid, d.department_head_uuid, d.tenant_id, d.department_name, " +
	                 "d.contact_email, d.contact_phone, d.description " +
	                 "FROM departments d " +
	                 "WHERE d.department_status = :status";

	    SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1);  // Only active departments
	    return template.query(sql, param, new DepartmentRowMapper());
	}
	
	
	@Transactional
	@Override
	public int deleteDepartment(String departmentId) {
		 if (departmentId == null || departmentId.trim().isEmpty()) {
	            throw new DepartmentIdNullException(HrManagementEnum.Department_id_null);
	        }
		 
		 try {
			 // Check if tenant exists by selecting the tenant_uuid column
	            String checkDepartmentExistsQuery = "SELECT department_uuid FROM departments WHERE department_uuid = :departmentId  AND department_status = :status";
	            MapSqlParameterSource param = new MapSqlParameterSource();
	            param.addValue("status", 1);
	            param.addValue("departmentId", departmentId);

	            // Execute query to check if tenant exists
	            List<String> departmentUuids = template.queryForList(checkDepartmentExistsQuery, param, String.class);

	            // If the tenant UUID doesn't exist in the result list, return -1 (tenant not found)
	            if (departmentUuids.isEmpty()) {
	                return -1; // Tenant not found
	            }

	            // Soft delete query
	            String softDelete = "UPDATE departments SET department_status = :status WHERE department_uuid = :departmentId";
	            param.addValue("status", 0);  // Assuming 0 represents deleted status
	            
	            // Perform the update (soft delete)
	            return template.update(softDelete, param);
	            
			 
		 }catch(Exception e){
				return 0;
		 }	

	}
	
	@Transactional	
	@Override
	public List<Employee> getAllEmployeesByDepartment(String departmentId) {

	    if (departmentId == null || departmentId.trim().isEmpty()) {
	        throw new DepartmentIdNullException(HrManagementEnum.Department_id_null);
	    }

	    try {
	        String sql = "SELECT e.tenant_id, e.department_id, e.role_id, e.employee_uuid, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title FROM employee e JOIN roles r ON e.role_id = r.role_uuid WHERE e.department_id = :departmentId AND r.role_name = :roleName AND e.employee_status = :status";
	        
	        SqlParameterSource param = new MapSqlParameterSource()
	                .addValue("departmentId", departmentId)
	                .addValue("roleName", "Employee")
	                .addValue("status", 1);

	        // Fetch the list of employees for the department
	        List<Employee> employees = template.query(sql, param, new EmployeeRowMapper());

	        // Return an empty list if no employees found
	        return employees != null ? employees : new ArrayList<>();
	    } catch (Exception e) {
	        // Log the exception and return an empty list if an error occurs
	        e.printStackTrace(); // It's important to log the exception for debugging
	        return new ArrayList<>();
	    }
	}

}  
  
	


