package com.isteer.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(EmployeeRepoDaoImpl.class); // Initialize logger

	
	@Transactional
    public Employee findByUserName(String userName) {
        logger.debug("Entering findByUserName method with userName: {}", userName);
        String sql = "SELECT employee_uuid,role_id, tenant_id, department_id, first_name,last_name, email, phone, address, date_of_joining, job_title, userName, password FROM employee WHERE userName = :userName";
        
        // Prepare parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userName", userName);
      
 
        // Query for the user data
        return template.queryForObject(sql, parameters, new EmployeeRowMapper());
    }
	
    @Transactional
    @Override
    public int registerEmployee(UserDetailsDto details, String departmentId) {
        logger.info("Registering employee with details: {}", details);

        String roleId = details.getRoleUuid();

        if (roleId == null || roleId.trim().isEmpty()) {
            logger.info("Role UUID not provided, fetching default 'Employee' role.");
            String getRoleIdQuery = "SELECT role_uuid, role_name, description FROM roles WHERE role_name = 'Employee' LIMIT 1";
            List<Roles> roleList = template.query(getRoleIdQuery, new RolesRowmapper());

            if (roleList != null && !roleList.isEmpty()) {
                roleId = roleList.get(0).getRoleUuid();  // Use the UUID for 'Employee' role
            } else {
                logger.error("Role not found for 'Employee'.");

                throw new IllegalArgumentException(HrManagementEnum.ILLEGAL_AGRUMENT); // Role not found
            }
        }

        String getTenantIdQuery = "SELECT tenant_id FROM departments WHERE department_uuid = :departmentUuid AND department_status = :status LIMIT 1";
        String tenantId = null;

        try {
            SqlParameterSource param = new MapSqlParameterSource()
                    .addValue("departmentUuid", departmentId)
                    .addValue("status", 1);
            tenantId = template.queryForObject(getTenantIdQuery, param, String.class);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Department not found for departmentId: {}", departmentId);

            throw new DepartmentNotFoundException(HrManagementEnum.DEPARTMENT_VALID_NOT_FOUND);
        }

        if (tenantId == null) {
            logger.error("Tenant ID is null for department: {}", departmentId);

            throw new TenantIdNullException(HrManagementEnum.No_list_of_tenansts);
        }

        UUID uuid = UUID.randomUUID();
        String employeeUuid = uuid.toString();

        // Map parameters for the employee
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("employeeId", employeeUuid)
                .addValue("roleId", roleId) // Use the correct roleId
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

        String registerUserQuery = "INSERT INTO employee (employee_uuid, role_id, tenant_id, department_id, userName, password, first_name, last_name, email, phone, address, date_of_joining, job_title) " +
                "VALUES (:employeeId, :roleId, :tenantId, :departmentId, :userName, :password, :firstName, :lastName, :email, :phone, :address, :joiningDate, :jobTitle)";

        int updateCount = template.update(registerUserQuery, param);

        logger.info("Employee registered with UUID: {}", employeeUuid);
        return updateCount;
    }


	@Transactional
	@Override
	public int addRole(Roles role) {
        logger.debug("Adding new role: {}", role.getRoleName());

		UUID uuid = UUID.randomUUID();
		String roleUuid = uuid.toString();
		String addRole = "INSERT INTO roles(role_uuid, role_name, description) VALUES (:roleId, :roleName, :description)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("roleId", roleUuid)
				.addValue("roleName", role.getRoleName()).addValue("description", role.getRoleDescription());
        logger.debug("Role added with UUID: {}", roleUuid);

		return template.update(addRole, param);

	}


	
	
	@Transactional
	@Override
	public List<Employee> getAllUsers() {
        logger.debug("Fetching all users.");

	    String sql = "SELECT e.employee_uuid, e.role_id, e.tenant_id, e.department_id, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title " +
	                 "FROM employee e JOIN departments d ON e.department_id = d.department_uuid JOIN tenants t ON e.tenant_id = t.tenant_uuid " +
	                 "WHERE e.employee_status = :status AND d.department_status = 1 AND t.tenant_status = 1";
	    SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1);
	    return template.query(sql, param, new EmployeeRowMapper());
	}

	
	@Transactional
	@Override
	public List<Employee> getEmployeesByTenant(String tenantUuid) {
	    String sql = "SELECT e.employee_uuid, e.role_id, e.tenant_id, e.department_id, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title " +
	                 "FROM employee e JOIN departments d ON e.department_id = d.department_uuid JOIN tenants t ON e.tenant_id = t.tenant_uuid " +
	                 "WHERE e.employee_status = :status AND d.department_status = 1 AND t.tenant_status = 1 AND e.tenant_id = :tenantId";
	    SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1).addValue("tenantId", tenantUuid);
	    return template.query(sql, param, new EmployeeRowMapper());
	}

	
	@Transactional
	@Override
	public List<Employee> getEmployeesByDepartment(String departmentUuid) {
	    String sql = "SELECT e.employee_uuid, e.role_id, e.tenant_id, e.department_id, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title " +
	                 "FROM employee e JOIN departments d ON e.department_id = d.department_uuid JOIN tenants t ON e.tenant_id = t.tenant_uuid " +
	                 "WHERE e.employee_status = :status AND d.department_status = 1 AND t.tenant_status = 1 AND e.department_id = :departmentId";
	    SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1).addValue("departmentId", departmentUuid);
	    return template.query(sql, param, new EmployeeRowMapper());
	}

	

	@Transactional
	@Override
	public List<Employee> getUsersById(String employeeUuid) {
		if (employeeUuid == null || employeeUuid.trim().isEmpty()) {
			throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
		}
		String sql = "SELECT e.employee_uuid, e.role_id, e.tenant_id, e.department_id, e.userName, e.password, e.first_name, e.last_name, e.email, e.phone, e.address, e.date_of_joining, e.job_title FROM employee e JOIN departments d ON e.department_id = d.department_uuid JOIN tenants t ON e.tenant_id = t.tenant_uuid WHERE e.employee_status = :status AND  e.employee_uuid = :employeeId AND d.department_status = 1 AND t.tenant_status = 1";
		SqlParameterSource param = new MapSqlParameterSource().addValue("status", 1).addValue("employeeId", employeeUuid);
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
        logger.debug("Updating user with ID: {}", details.getEmployeeUuid());

		if (details.getEmployeeUuid() == null || details.getEmployeeUuid().trim().isEmpty()) {
            logger.error("Employee UUID is null or empty.");

			throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
		}
		Optional<Employee> existingUser = findById(details.getEmployeeUuid());
		if (!existingUser.isPresent()) {
            logger.warn("Employee with ID: {} does not exist.", details.getEmployeeUuid());

			return -1;
		}

		String sql = "UPDATE employee SET userName = :userName, password = :password, first_name = :firstName, last_name = :lastName, email = :email, phone = :phone, address = :address, date_of_joining = :dateOfJoining, job_title = :jobTitle WHERE employee_status = :status AND employee_uuid = :employeeId";

		SqlParameterSource param = new MapSqlParameterSource().addValue("employeeId", details.getEmployeeUuid())
				.addValue("userName", details.getUserName())
				.addValue("password", details.getPassword()).addValue("firstName", details.getFirstName())
				.addValue("lastName", details.getLastName()).addValue("email", details.getEmail())
				.addValue("phone", details.getPhoneNumber()).addValue("address", details.getAddress())
				.addValue("dateOfJoining", details.getDateOfJoining()).addValue("jobTitle", details.getJobTitle())
				.addValue("status", 1);
		try {
            logger.info("User with ID: {} updated successfully.", details.getEmployeeUuid());
			return template.update(sql, param);
		} catch (DataAccessException e) {
			
            logger.error("Error updating employee with ID: {}", details.getEmployeeUuid(), e);
			return 0; // Indicating failure to update
		}
	}

	@Transactional
	@Override
	public int deleteEmployee(String employeeId) {
        logger.debug("Attempting to delete employee with ID: {}", employeeId);

		if (employeeId == null || employeeId.trim().isEmpty()) {
            logger.error("Employee ID is null or empty.");

			throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
		}

		try {
			String checkEmployeeExistsQuery = "SELECT employee_uuid FROM employee WHERE employee_uuid = :employeeId AND employee_status = :status ";
			MapSqlParameterSource param = new MapSqlParameterSource();
			param.addValue("status", 1);
			param.addValue("employeeId", employeeId);

			List<String> employeeUuids = template.queryForList(checkEmployeeExistsQuery, param, String.class);
			if (employeeUuids.isEmpty()) {
                logger.warn("No employee found with ID: {}", employeeId);

				return -1; // employee not found
			}
			String softDelete = "UPDATE employee SET employee_status = :status WHERE employee_uuid = :employeeId";
			param.addValue("status", 0); // Assuming 0 represents deleted status
            logger.info("Employee with ID: {} marked as deleted", employeeId);

			// Perform the update (soft delete)
			return template.update(softDelete, param);

		} catch (Exception e) {
            logger.error("Error deleting employee with ID: {}", employeeId);
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
            logger.error("Employee ID is null or empty.");

	        throw new EmployeeIdNullException(HrManagementEnum.Employee_id_null);
	    }
	    
	    Employee employee = new Employee();
	    employee.setEmployeeUuid(employeeId); // Correctly set employeeId
	    employee.setRoleUuid(roleId);          // Correctly set roleId
	    
	    Optional<Employee> existingUser = findById(employee.getEmployeeUuid());
	    if (!existingUser.isPresent()) {
            logger.warn("No employee found with ID: {}", employeeId);

	        return -1;
	    }

	    // Validate roleId
	    if (roleId.trim().isBlank()) {
            logger.warn("Roles Not Blank with ID: {}", roleId);

	        throw new RoleIdNullException(HrManagementEnum.Role_id_null);
	    }

	    String getRoleIdQuery = "SELECT role_uuid, role_name, description FROM roles WHERE role_uuid = :roleId";
	    SqlParameterSource param = new MapSqlParameterSource().addValue("roleId", roleId);
	    List<Roles> roleList = template.query(getRoleIdQuery, param, new RolesRowmapper());

	    if (roleList != null && !roleList.isEmpty()) {
	        roleId = roleList.get(0).getRoleUuid();
	    } else {

	        throw new IllegalArgumentException(HrManagementEnum.Illegal_Argumnet_role);
	    }

	    String sql = "UPDATE employee SET role_id = :roleId WHERE employee_status = :status AND employee_uuid = :employeeId";
	    SqlParameterSource paramUpdate = new MapSqlParameterSource()
	            .addValue("roleId", roleId)
	            .addValue("status", 1)
	            .addValue("employeeId", employee.getEmployeeUuid());

	    try {
            logger.info("Employee with ID: {} elevated eith roleId", employeeId, roleId);

	        return template.update(sql, paramUpdate);
	    } catch (DataAccessException e) {
            logger.error("Error updating employee with ID: {}",employeeId);

	        return 0;
	    }
	}


}
