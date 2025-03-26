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

import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.entity.Roles;
import com.isteer.enums.HrManagementEnum;
import com.isteer.exception.DepartmentIdNullException;
import com.isteer.exception.DepartmentNotFoundException;
import com.isteer.exception.EmployeeIdNullException;
import com.isteer.exception.IllegalArgumentException;
import com.isteer.exception.RoleIdNullException;
import com.isteer.exception.TenantIdNullException;
import com.isteer.repository.dao.EmployeeRepoDao;
import com.isteer.util.EmployeeRowMapper;
import com.isteer.util.RolesRowmapper;

@Component
public class EmployeeRepoDaoImpl implements EmployeeRepoDao {

	@Autowired
	NamedParameterJdbcTemplate template;

	
	 // Method to find user by username
    public Employee findByUserName(String userName) {
        // SQL query to fetch user details
        String sql = "SELECT employee_uuid,role_id, tenant_id, department_id, first_name,last_name, email, phone, address, date_of_joining, job_title, userName, password FROM employee WHERE userName = :userName";
        
        // Prepare parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userName", userName);
      

        // Query for the user data
        return template.queryForObject(sql, parameters, new EmployeeRowMapper());
    }
	
	@Transactional
    @Override
    public int registerEmployees(List<UserDetailsDto> detailsList, String departmentId) {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new DepartmentIdNullException(HrManagementEnum.Department_id_null);
        }

        // Fetch the role_uuid for the 'Employee' role
        String getRoleIdQuery = "SELECT role_uuid, role_name, description FROM roles WHERE role_name = 'Employee' LIMIT 1";
        List<Roles> roleList = template.query(getRoleIdQuery, new RolesRowmapper());

        String roleId = null;
        if (roleList != null && !roleList.isEmpty()) {
            roleId = roleList.get(0).getRoleId();
        } else {
            throw new IllegalArgumentException(HrManagementEnum.ILLEGAL_AGRUMENT);
        }

        // Fetch tenant_id from the department table using the department_uuid
        String getTenantIdQuery = "SELECT tenant_id FROM departments WHERE department_uuid = :departmentUuid AND department_status = :status LIMIT 1";
        String tenantId = null;

        try {
            SqlParameterSource param = new MapSqlParameterSource()
                    .addValue("departmentUuid", departmentId)
                    .addValue("status", 1);
            tenantId = template.queryForObject(getTenantIdQuery, param, String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new DepartmentNotFoundException(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND);
        }

        if (tenantId == null) {
            throw new IllegalArgumentException(HrManagementEnum.No_list_of_tenansts);
        }

        // Create list of parameter maps for batch insertion
        List<MapSqlParameterSource> batchParams = new ArrayList<>();

        for (UserDetailsDto details : detailsList) {
            UUID uuid = UUID.randomUUID();
            String employeeUuid = uuid.toString();

            // Map parameters for each employee
            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue("employeeId", employeeUuid)
                    .addValue("roleId", roleId)
                    .addValue("tenantId", tenantId)
                    .addValue("departmentId", departmentId)
                    .addValue("userName", details.getUserName())
                    .addValue("password", details.getPassword())
                    .addValue("firstName", details.getFirstName())
                    .addValue("lastName", details.getLastName())
                    .addValue("email", details.getEmail())
                    .addValue("phone", details.getPhoneNumber())
                    .addValue("address", details.getAddress())
                    .addValue("joiningDate", details.getDateOfJoining())
                    .addValue("jobTitle", details.getJobTitle());

            batchParams.add(param);
        }

        // Batch insert the employees
        String registerUserQuery = "INSERT INTO employee (employee_uuid, role_id, tenant_id, department_id, userName, password, first_name, last_name, email, phone, address, date_of_joining, job_title) " +
                "VALUES (:employeeId, :roleId, :tenantId, :departmentId, :userName, :password, :firstName, :lastName, :email, :phone, :address, :joiningDate, :jobTitle)";

        // Batch update
        int[] updateCounts = template.batchUpdate(registerUserQuery, batchParams.toArray(new MapSqlParameterSource[0]));

        // Return total number of rows affected
        return updateCounts.length;
    }

	@Transactional
	@Override
	public int addRole(Roles role) {
		UUID uuid = UUID.randomUUID();
		String roleUuid = uuid.toString();
		String addRole = "INSERT INTO roles(role_uuid, role_name, description) VALUES (:roleId, :roleName, :description)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("roleId", roleUuid)
				.addValue("roleName", role.getRoleName()).addValue("description", role.getRoleDescription());
		return template.update(addRole, param);

	}

	@Transactional
	@Override
	public List<Employee> getAllUsers() {
		String sql = "SELECT e.employee_uuid, e.role_id, e.tenant_id, e.department_id, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title FROM employee e JOIN departments d ON e.department_id = d.department_uuid JOIN tenants t ON e.tenant_id = t.tenant_uuid WHERE e.employee_status = :status AND d.department_status = 1 AND t.tenant_status = 1";
		SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1);
		return template.query(sql, param, new EmployeeRowMapper());
	}

	@Transactional
	@Override
	public List<Employee> getUsersById(String employeeId) {
		if (employeeId == null || employeeId.trim().isEmpty()) {
			throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
		}
		String sql = "SELECT e.employee_uuid, e.role_id, e.tenant_id, e.department_id, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title FROM employee e JOIN departments d ON e.department_id = d.department_uuid JOIN tenants t ON e.tenant_id = t.tenant_uuid WHERE e.employee_status = :status AND  e.employee_uuid = :employeeId AND d.department_status = 1 AND t.tenant_status = 1";
		SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1).addValue("employeeId", employeeId);
		return template.query(sql, param, new EmployeeRowMapper());

	}

	@Transactional
	// Method to check if a tenant exists by tenantId
	public Optional<Employee> findById(String userId) {
		String sql = "SELECT employee_uuid, role_id, tenant_id, department_id, userName, password, first_name, last_name, email, phone, address, date_of_joining, job_title FROM employee WHERE employee_status = :status AND employee_uuid = :employeeId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("employeeId", userId).addValue("status", 1);

		try {
			Employee employee = template.queryForObject(sql, param, new EmployeeRowMapper());
			return Optional.ofNullable(employee);
		} catch (EmptyResultDataAccessException e) {
			// Return an empty Optional if no tenant found
			return Optional.empty();
		}
	}

	@Transactional
	@Override
	public int updateUser(UserDetailsDto details) {
		if (details.getEmployeeId() == null || details.getEmployeeId().trim().isEmpty()) {
			throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
		}
		Optional<Employee> existingUser = findById(details.getEmployeeId());
		if (!existingUser.isPresent()) {
			return -1;
		}

		String sql = "UPDATE employee SET userName = :userName, password = :password, first_name = :firstName, last_name = :lastName, email = :email, phone = :phone, address = :address, date_of_joining = :dateOfJoining, job_title = :jobTitle WHERE employee_status = :status AND employee_uuid = :employeeId";

		SqlParameterSource param = new MapSqlParameterSource().addValue("employeeId", details.getEmployeeId())
				.addValue("userName", details.getUserName())
				.addValue("password", details.getPassword()).addValue("firstName", details.getFirstName())
				.addValue("lastName", details.getLastName()).addValue("email", details.getEmail())
				.addValue("phone", details.getPhoneNumber()).addValue("address", details.getAddress())
				.addValue("dateOfJoining", details.getDateOfJoining()).addValue("jobTitle", details.getJobTitle())
				.addValue("status", 1);
		try {
			// Execute the update query
			return template.update(sql, param);
		} catch (DataAccessException e) {
			e.printStackTrace();
			// Log any database access errors and return 0 or throw a custom exception
//	            log.error("Error updating tenant in the database: {}", e.getMessage());
			System.out.println("employee update");
			return 0; // Indicating failure to update
		}
	}

	@Transactional
	@Override
	public int deleteEmployee(String employeeId) {

		if (employeeId == null || employeeId.trim().isEmpty()) {
			throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
		}

		try {
			String checkEmployeeExistsQuery = "SELECT employee_uuid FROM employee WHERE employee_uuid = :employeeId AND employee_status = :status ";
			MapSqlParameterSource param = new MapSqlParameterSource();
			param.addValue("status", 1);
			param.addValue("employeeId", employeeId);

			List<String> employeeUuids = template.queryForList(checkEmployeeExistsQuery, param, String.class);
			if (employeeUuids.isEmpty()) {
				return -1; // employee not found
			}
			String softDelete = "UPDATE employee SET employee_status = :status WHERE employee_uuid = :employeeId";
			param.addValue("status", 0); // Assuming 0 represents deleted status

			// Perform the update (soft delete)
			return template.update(softDelete, param);

		} catch (Exception e) {
			// Log exception here (optional), rethrow or handle the exception
			return 0; // Indicates failure
		}
	}
	
	@Transactional
	@Override
	public List<Roles> getAllAvailableRoles() {
		String sql = "SELECT role_uuid, role_name, description FROM roles WHERE role_status = :status";
		SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1);
		return template.query(sql, param, new RolesRowmapper());
		
	}

	@Transactional
	@Override
	public int updateUserRole(String employeeId, String roleId) {
	    if (employeeId.isBlank()) {
	        throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
	    }
	    
	    Employee employee = new Employee();
	    employee.setEmployeeId(employeeId); // Correctly set employeeId
	    employee.setRoleId(roleId);          // Correctly set roleId
	    
	    Optional<Employee> existingUser = findById(employee.getEmployeeId());
	    if (!existingUser.isPresent()) {
	        return -1;
	    }

	    // Validate roleId
	    if (roleId.trim().isBlank()) {
	        throw new RoleIdNullException(HrManagementEnum.Role_id_null);
	    }

	    String getRoleIdQuery = "SELECT role_uuid, role_name, description FROM roles WHERE role_uuid = :roleId";
	    SqlParameterSource param = new MapSqlParameterSource().addValue("roleId", roleId);
	    List<Roles> roleList = template.query(getRoleIdQuery, param, new RolesRowmapper());

	    if (roleList != null && !roleList.isEmpty()) {
	        roleId = roleList.get(0).getRoleId();
	    } else {

	        throw new IllegalArgumentException(HrManagementEnum.Illegal_Argumnet_role);
	    }

	    String sql = "UPDATE employee SET role_id = :roleId WHERE employee_status = :status AND employee_uuid = :employeeId";
	    SqlParameterSource paramUpdate = new MapSqlParameterSource()
	            .addValue("roleId", roleId)
	            .addValue("status", 1)
	            .addValue("employeeId", employee.getEmployeeId());

	    try {
	        return template.update(sql, paramUpdate);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return 0;
	    }
	}


}
