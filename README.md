
# 💼 Employee Salary Management Web Service

This project is a web service that is developed to help HR departments manage their employee information.
Each employee contains the following information:
| Information | Type     | Description                |
| :-------- | :------- | :------------------------- |
| id | `string` | unique alphanumeric ID assigned by the company. |
| login | `string` | unique alphanumeric login assigned by the company. |
| name | `string` | The name of the employee. |
| startDate | `LocalDate` | Employee start date. |
| salary | `double` | Employee salary.  |

The web service will be able to receive requests to create, update, read and delete employee information.
It will also have the ability to allow list of employees to be retrieved and sorted accordingly to the field type.
The service also supports an upload API which accepts a CSV file so that group Employee information can be updated/created at the same time.

## Usage
#### 💻 How to setup
Clone the repository:
Run command:
```
https://github.com/AnZhiThen/EmployeeSalaryManagement.git
```
#### 🏗️ Creating the build:
Run command:
```
gradlew build
```
Upon successful build, latest test coverage report can be found in:

📋 `employeeSalaryManagement/build/reports/tests/test/index.html`

#### 🚀 Launching the service
Run command:
```
gradlew bootRun
```
The service will be run on `http://localhost:8080/`

#### 📚 Executing the unit test
Run command:
```
gradlew test -t
```

##  Docker setup

#### Prerequisites:

✔️ Have docker installed:  [Docker](https://docs.docker.com/desktop/)

✔️ Created the build in ([Build instructions](#creating-the-build))

#### Building the docker image:
```
docker build --build-arg JAR_FILE=build/libs/\*.jar -t employee-salary-service
```
#### Launching the docker image with a container name:
```
docker run --name employee-docker -p 8080:8080 employee-salary-service
```

## API Reference

#### Add/Update multiple employees at the same time

```
POST /users/upload
```
| Request Details | Description     |
| :-------- | :------- |
| Request Content Type | multipart/form-data     |
| Request Params | CSV file     |
| Success Response Status |   200 - Data uploaded  |
| Failure Response Status |   400 - Upload failed  |

Validation Rules
- All 5 columns must be filled.
- Salary cannot be negative.
- Any row starting with “#” is considered a comment and ignored
- Details that exists in the current database will be updated
- ID and Login must be unique
- If there is an error the whole upload will fail 

##### Sample CSV:
 |  id  |  login  |  name  |  salary  |  startDate  | 
 | :-------- | :------- | :------- | :------- | :------- |
 | e0001 | hpotter | Harry Potter | 1234.00 | 16-Nov-01 | 
 | e0002 | rwesley | Ron Weasley | 19234.50 | 2001-11-16 | 
 | e0003 | ssnape | Severus Snape | 4000.0 | 2001-11-16 | 
 | e0004 | rhagrid | Rubeus Hagrid | 3999.999 | 16-Nov-01 |
 | e0005 | voldemort | Lord Voldemort | 523.4 | 17-Nov-01 |
 | e0006 | gwesley | Ginny Weasley | 4000.004 | 18-Nov-01 | 

#### Read Multiple Employee Details

```
GET /users
```
| Request Details | Description     |
| :-------- | :------- |
| Request Content Type | JSON     |
| Success Response Status |   200 - Employee data retrieved  |
| Failure Response Status |   400 - Bad Parameters  |

Request Params (All fields are **optional** to filter data)
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `minSalary` | `double` | return only if salary >= minimum salary. Default is 0. |
| `maxSalary` | `double` | return only if salary < maximum salary. Default is 4000.00. |
| `offset` | `int` | starting offset of results to return. Default is 0. |
| `limit` | `int` | max number of results to return. Default is 50. |
| `sort` | `string` | can be any of the fields in employee. Default is `id`. |
| `order` | `string` | max number of results to return. Default is 0 => no limit. |

#### Create / Update Employee Details
Create:
```http
POST /users
```

Update:
```http
PUT /users/{id}
```

| Request Details | Description     |
| :-------- | :------- |
| Request Content Type | JSON     |
| Success Response Status |   201/200 - Employee record created/updated  |
| Failure Response Status |   400 - Bad Parameters |

Request Body (All fields **required**)
| Parameter | Type     | Description                | Updatable |
| :-------- | :------- | :------------------------- | :------- |
| id | `string` | unique alphanumeric ID assigned by the company. | ❌|
| login | `string` | unique alphanumeric login assigned by the company. |✔️|
| name | `string` | the name of the employee. |✔️|
| startDate | `LocalDate` | employee start date. Accepted formats: (dd-MMM-yyy) or (dd-MMM-yy) | ❌ |
| salary | `double` | employee salary. Must not be negative.  |✔️|

- For update, if employee id not found status 400 will be returned.

#### Read / Delete Employee Details
Read:
```http
GET /users/{id}
```

Delete:
```http
DELETE /users/{id}
```

| Request Details | Description     |
| :-------- | :------- |
| Request Content Type | JSON     |
| Success Response Status |   200 - Employee record retrieved/deleted  |
| Failure Response Status |   400 - Employee not found |

Request Params (All fields **required**)
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| id | `string` | unique alphanumeric ID assigned by the company.|
