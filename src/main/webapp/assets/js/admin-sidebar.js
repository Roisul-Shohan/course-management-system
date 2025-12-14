

function loadStats() {
    fetch('/AdminStatsServlet')
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {

            const totalStudents = document.getElementById('total-students');
            const totalTeachers = document.getElementById('total-teachers');
            const totalCourses = document.getElementById('total-courses');

            if (totalStudents) {
                totalStudents.textContent = data.totalStudents;
            } else {
                console.error('loadStats: total-students element not found');
            }

            if (totalTeachers) {
                totalTeachers.textContent = data.totalTeachers;
            } else {
                console.error('loadStats: total-teachers element not found');
            }

            if (totalCourses) {
                totalCourses.textContent = data.totalCourses;
            } else {
                console.error('loadStats: total-courses element not found');
            }

        })
        .catch(error => {
            console.error('loadStats: Error occurred during stats loading:', error);
            console.error('loadStats: Error details:', error.message);
            
            const totalStudentsEl = document.getElementById('total-students');
            const totalTeachersEl = document.getElementById('total-teachers');
            const totalCoursesEl = document.getElementById('total-courses');

            if (totalStudentsEl) {
                totalStudentsEl.textContent = 'Error';
            }
            if (totalTeachersEl) {
                totalTeachersEl.textContent = 'Error';
            }
            if (totalCoursesEl) {
                totalCoursesEl.textContent = 'Error';
            }
        });
}

function showSection(sectionId) {
    
    const section = document.getElementById(sectionId);
    if (section) {
        section.classList.remove('hidden');
    } else {
        document.getElementById('default-content').classList.remove('hidden');
    }
}

function initAdminSidebar() {
    loadStats();

    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('sidebar-link') || e.target.closest('.sidebar-link')) {
            const link = e.target.classList.contains('sidebar-link') ? e.target : e.target.closest('.sidebar-link');
            e.preventDefault();

            document.querySelectorAll('.sidebar-link').forEach(l => {
                l.classList.remove('bg-blue-600', 'text-white');
                l.classList.add('text-gray-300');
            });

            link.classList.add('bg-blue-600', 'text-white');
            link.classList.remove('text-gray-300');

            document.querySelectorAll('#main-content > div').forEach(div => div.classList.add('hidden'));

            const section = link.getAttribute('data-section');
            switch (section) {
                case 'students':
                    loadStudents();
                    showSection(section + '-content');
                    break;
                case 'teachers':
                    loadTeachers();
                    showSection(section + '-content');
                    break;
                case 'courses':
                    loadCourses();
                    showSection(section + '-content');
                    break;
                case 'assign-teacher':
                    loadCoursesForSelect();
                    loadTeachersForSelect();
                    showSection(section + '-content');
                    break;
                case 'add-course':
                    console.log('Add course case called');
                    showSection(section + '-content');
                    break;
                case 'dashboard':
                    showSection('default-content');
                    break;
                default:
                    showSection('default-content');
                    break;
            }
        }
    });

    // Modal functionality
    const modal = document.getElementById('student-modal');
    const closeModalBtn = document.getElementById('close-modal');

    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', function () {
            modal.classList.add('hidden');
        });
    }

    if (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target === modal) {
                modal.classList.add('hidden');
            }
        });
    }
}


function loadStudents() {
    fetch('AdminStudentServlet')
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const studentsList = document.getElementById('students-list');
            studentsList.innerHTML = '';

            if (data.length === 0) {
                studentsList.innerHTML = '<p class="text-gray-400">No students found.</p>';
                return;
            }

            data.forEach(student => {
                const studentDiv = document.createElement('div');
                studentDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                studentDiv.innerHTML = `
                    <span class="font-medium">${student.username}</span>
                    <button class="view-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-student='${JSON.stringify(student)}'>
                        View Details
                    </button>
                `;
                studentsList.appendChild(studentDiv);
            });

            document.querySelectorAll('.view-details-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const student = JSON.parse(this.getAttribute('data-student'));
                    showStudentModal(student);
                });
            });
        })
        .catch(error => {
            console.error('Error loading students:', error);
            document.getElementById('students-list').innerHTML =
                '<p class="text-red-400">Error loading students. Please try again.</p>';
        });
}

function showStudentModal(student) {
    const modalFullname = document.getElementById('modal-fullname');
    const modalEmail = document.getElementById('modal-email');
    const modalCourses = document.getElementById('modal-courses');
    const studentModal = document.getElementById('student-modal');

    if (modalFullname) {
        modalFullname.textContent = student.fullname;
    } else {
        console.error('showStudentModal: modal-fullname element not found');
    }

    if (modalEmail) {
        modalEmail.textContent = student.email;
    } else {
        console.error('showStudentModal: modal-email element not found');
    }

    if (modalCourses) {
        modalCourses.textContent = student.courses && student.courses.length > 0 ? student.courses.join(', ') : 'No courses registered';
    } else {
        console.error('showStudentModal: modal-courses element not found');
    }

    if (studentModal) {
        studentModal.classList.remove('hidden');
    } else {
        console.error('showStudentModal: student-modal element not found');
    }
}

const teacherModal = document.getElementById('teacher-modal');
const closeTeacherModalBtn = document.getElementById('close-teacher-modal');

if (closeTeacherModalBtn) {
    closeTeacherModalBtn.addEventListener('click', function () {
        teacherModal.classList.add('hidden');
    });
}

if (teacherModal) {
    teacherModal.addEventListener('click', function (e) {
        if (e.target === teacherModal) {
            teacherModal.classList.add('hidden');
        }
    });
}

function loadTeachers() {
    fetch('AdminTeacherServlet')
        .then(response => response.json())
        .then(data => {
            const teachersList = document.getElementById('teachers-list');
            teachersList.innerHTML = '';

            if (data.length === 0) {
                teachersList.innerHTML = '<p class="text-gray-400">No teachers found.</p>';
                return;
            }

            data.forEach(teacher => {
                const teacherDiv = document.createElement('div');
                teacherDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                teacherDiv.innerHTML = `
                    <span class="font-medium">${teacher.username}</span>
                    <button class="view-teacher-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-teacher='${JSON.stringify(teacher)}'>
                        View Details
                    </button>
                `;
                teachersList.appendChild(teacherDiv);
            });

            document.querySelectorAll('.view-teacher-details-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const teacher = JSON.parse(this.getAttribute('data-teacher'));
                    showTeacherModal(teacher);
                });
            });
        })
        .catch(error => {
            console.error('Error loading teachers:', error);
            document.getElementById('teachers-list').innerHTML =
                '<p class="text-red-400">Error loading teachers. Please try again.</p>';
        });
}


function showTeacherModal(teacher) {
    document.getElementById('teacher-modal-fullname').textContent = teacher.fullname;
    document.getElementById('teacher-modal-email').textContent = teacher.email;
    document.getElementById('teacher-modal-courses').textContent =
        teacher.courses && teacher.courses.length > 0 ? teacher.courses.join(', ') : 'No courses assigned';

    document.getElementById('teacher-modal').classList.remove('hidden');
}

const courseModal = document.getElementById('course-modal');
const closeCourseModalBtn = document.getElementById('close-course-modal');

if (closeCourseModalBtn) {
    closeCourseModalBtn.addEventListener('click', function () {
        courseModal.classList.add('hidden');
    });
}

if (courseModal) {
    courseModal.addEventListener('click', function (e) {
        if (e.target === courseModal) {
            courseModal.classList.add('hidden');
        }
    });
}


function loadCourses() {
    fetch('AdminCourseServlet')
        .then(response => response.json())
        .then(data => {
            const coursesList = document.getElementById('courses-list');
            coursesList.innerHTML = '';

            if (data.length === 0) {
                coursesList.innerHTML = '<p class="text-gray-400">No courses found.</p>';
                return;
            }

            data.forEach(course => {
                const courseDiv = document.createElement('div');
                courseDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                courseDiv.innerHTML = `
                   <div>
                    <span class="font-medium">${course.name}</span>
                    <span class="text-sm text-gray-400 ml-2">(${course.courseCode})</span>

                    <div class="text-sm text-gray-400">
                        ${
                        course.assignedTeacher
                            ? `Assigned Teacher:
                            <span class="text-pink-400 font-semibold">
                                ${course.assignedTeacher}
                            </span>`
                            : `<a href="#"
                                class="assign-teacher-link text-blue-400 hover:text-blue-300"
                                data-course-id="${course.id}">
                                Assign Teacher
                            </a>`
                        }
                        • <a href="#"
                            class="view-students-link text-green-400 hover:text-green-300"
                            data-course-id="${course.id}">
                            View Students (${course.students || 0})
                        </a>
                    </div>
                    </div>

                    <button class="view-course-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-course='${JSON.stringify(course)}'>
                        View Details
                    </button>
                `;
                coursesList.appendChild(courseDiv);
            });

            document.querySelectorAll('.assign-teacher-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const courseId = this.getAttribute('data-course-id');
                    document.querySelector('[data-section="assign-teacher"]').click();
                });
            });

            document.querySelectorAll('.view-students-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const courseId = this.getAttribute('data-course-id');
                    loadCourseStudents(courseId);
                });
            });

            document.querySelectorAll('.view-course-details-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const course = JSON.parse(this.getAttribute('data-course'));
                    showCourseModal(course);
                });
            });
        })
        .catch(error => {
            console.error('Error loading courses:', error);
            document.getElementById('courses-list').innerHTML =
                '<p class="text-red-400">Error loading courses. Please try again.</p>';
        });
}


function loadCoursesForSelect() {
    fetch('AdminCourseServlet')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('assign-course-select');
            select.innerHTML = '<option value="">Select Course</option>';
            data.forEach(course => {
                const option = document.createElement('option');
                option.value = course.id;
                option.textContent = course.name + ' (' + course.courseCode + ')';
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading courses for select:', error);
        });
}

function loadTeachersForSelect() {
    fetch('AdminTeacherServlet')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('assign-teacher-select');
            select.innerHTML = '<option value="">Select Teacher</option>';
            data.forEach(teacher => {
                const option = document.createElement('option');
                option.value = teacher.id;
                option.textContent = teacher.fullname + ' (' + teacher.username + ')';
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading teachers for select:', error);
        });
}

function showCourseModal(course) {
    document.getElementById('course-modal-name').textContent = course.name;
    document.getElementById('course-modal-code').textContent = course.courseCode;
    document.getElementById('course-modal-teacher').textContent = course.assignedTeacher || 'Not Assigned';
    document.getElementById('course-modal-students').textContent = course.students || 0;

    document.getElementById('course-modal').classList.remove('hidden');
}

function loadCourseStudents(courseId) {
    fetch('AdminCourseStudentsServlet?courseId=' + courseId)
        .then(response => response.json())
        .then(data => {
            const courseStudentsList = document.getElementById('course-students-list');
            courseStudentsList.innerHTML = '';

            if (data.success) {
                if (data.students.length === 0) {
                    courseStudentsList.innerHTML = '<p class="text-gray-400">No students enrolled in this course.</p>';
                } else {

                    data.students.forEach(student => {
                        const studentDiv = document.createElement('div');
                        studentDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                        studentDiv.innerHTML = `
                            <div>
                                <span class="font-medium">${student.fullname}</span>
                                <span class="text-sm text-gray-400 ml-2">(${student.username})</span>
                            </div>
                            <span class="text-sm text-gray-400">${student.email}</span>
                        `;
                        courseStudentsList.appendChild(studentDiv);
                    });
                }

                document.querySelectorAll('#main-content > div').forEach(section => section.classList.add('hidden'));
                document.getElementById('course-students-content').classList.remove('hidden');
            } else {
                alert('Error: ' + (data.message || 'Failed to load students'));
            }
        })
        .catch(error => {
            console.error('Error loading course students:', error);
            alert('Error loading course students. Please try again.');
        });
}

function initAddCourseForm() {
    const addCourseForm = document.getElementById('add-course-form');
    if (addCourseForm) {
        addCourseForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const formData = new FormData(this);
            const courseName = formData.get('courseName');
            const courseCode = formData.get('courseCode');

            if (!courseName || !courseCode) {
                alert('Please fill in all fields');
                return;
            }

            const params = new URLSearchParams();
            params.append('courseName', courseName);
            params.append('courseCode', courseCode);

            fetch('AddCourseServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
                .then(async response => {
                    const text = await response.text();
                    return JSON.parse(text);
                })
                .then(data => {
                    if (data.success) {
                        alert('Course added successfully!');
                        addCourseForm.reset();
                        if (document.getElementById('courses-content').classList.contains('hidden') === false) {
                            loadCourses();
                        }
                    } else {
                        alert('Error: ' + (data.error || 'Failed to add course'));
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error adding course. Please try again.');
                });
        });
    }
}


function initAssignTeacherForm() {
    const assignTeacherForm = document.getElementById('assign-teacher-form');
    if (assignTeacherForm) {
        assignTeacherForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const courseId = document.getElementById('assign-course-select').value;
            const teacherId = document.getElementById('assign-teacher-select').value;

            if (!courseId || !teacherId || courseId === "" || teacherId === "") {
                alert('Please select both course and teacher');
                return;
            }

            const params = new URLSearchParams();
            params.append('courseId', courseId);
            params.append('teacherId', teacherId);

            fetch('AssignTeacherServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Teacher assigned successfully!');
                        assignTeacherForm.reset();

                        if (document.getElementById('courses-content').classList.contains('hidden') === false) {
                            loadCourses();
                        }
                    } else {
                        alert('Error: ' + (data.error || 'Failed to assign teacher'));
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error assigning teacher. Please try again.');
                });
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    initAdminSidebar();
    initAddCourseForm();
    initAssignTeacherForm();
    loadCoursesForSelect();
    loadTeachersForSelect();
});