const projects = [
  {
    title: 'Buildroot 32-bit Platform',
    category: 'Embedded',
    file: '10-strike.com%20build%20tools%20sd.md',
    description: 'Buildroot setup for a custom 32-bit Linux platform and 10-Strike tool integration with Wine.',
    tags: ['Buildroot', 'Linux', 'Embedded', 'Wine']
  },
  {
    title: 'Web Server Security Stack',
    category: 'Web',
    file: 'Free%20Website%20SD.md',
    description: 'A secure web server plan using Ubuntu, Nginx, SSL, MySQL, and monitoring tools for production readiness.',
    tags: ['Nginx', 'Apache', 'SSL', 'Security']
  },
  {
    title: 'Network Monitoring & SNMP',
    category: 'Network',
    file: 'Discover%20network%20snmp%20Django%20python%20mysql%20html%20sd.md',
    description: 'Monitoring architecture for network discovery, SNMP polling, and dashboard-based visibility.',
    tags: ['SNMP', 'Django', 'Monitoring', 'Network']
  },
  {
    title: 'Cacti + Nagios Monitoring',
    category: 'Monitoring',
    file: 'Cacti%20nagios%20SD.md',
    description: 'System monitoring and graphing for infrastructure health, availability, and service tracking.',
    tags: ['Cacti', 'Nagios', 'Ops', 'Linux']
  },
  {
    title: 'Django Portal Setup',
    category: 'Application',
    file: 'بيانات%20المواطنين%20Django%20python%20html%20mysql.md',
    description: 'A Django-based citizen information platform with HTML, Python, and MySQL integration.',
    tags: ['Django', 'MySQL', 'Python', 'Portal']
  },
  {
    title: 'Smart Meter System',
    category: 'IoT',
    file: 'Smart%20meter%20sd.md',
    description: 'Smart-state metering and remote data capture design with infrastructure and reporting requirements.',
    tags: ['Smart Meter', 'Telemetry', 'IoT', 'Data']
  },
  {
    title: 'OpenWRT Device Firmware',
    category: 'Embedded',
    file: 'OpenWRT%20firmware%20new%20device.md',
    description: 'Firmware planning for new-device deployment using OpenWRT and custom configuration workflows.',
    tags: ['OpenWRT', 'Firmware', 'Router', 'Embedded']
  },
  {
    title: 'Android APN & Device Configuration',
    category: 'Mobile',
    file: 'Hope%206g%20apn.md',
    description: 'APN and mobile connectivity setup notes for Android, telecommunications, and carrier profiles.',
    tags: ['Android', 'APN', 'Telecom', 'Mobile']
  },
  {
    title: 'Ubuntu Server & Monitoring',
    category: 'Linux',
    file: 'Ubuntu%20server%20Catci%20SD.md',
    description: 'Ubuntu server hardening and monitoring guidance using Cacti and server management practices.',
    tags: ['Ubuntu', 'Server', 'Monitoring', 'Linux']
  },
  {
    title: 'Project Completion Template',
    category: 'Documentation',
    file: 'docs/project-template.md',
    description: 'Reusable template for converting rough notes into complete, maintainable project documentation.',
    tags: ['Template', 'Docs', 'Project', 'Planning']
  }
];

const projectGrid = document.getElementById('project-grid');
const searchInput = document.getElementById('project-search');
const categoryFilter = document.getElementById('category-filter');
const projectCount = document.getElementById('project-count');
const categoryCount = document.getElementById('category-count');

const categories = [...new Set(projects.map((project) => project.category))].sort();

categories.forEach((category) => {
  const option = document.createElement('option');
  option.value = category;
  option.textContent = category;
  categoryFilter.appendChild(option);
});

function renderProjects(items) {
  projectGrid.innerHTML = '';

  if (!items.length) {
    projectGrid.innerHTML = '<article class="project-card"><p>No projects match your search.</p></article>';
    return;
  }

  items.forEach((project) => {
    const card = document.createElement('article');
    card.className = 'project-card';

    const tags = project.tags
      .map((tag) => `<span>${tag}</span>`)
      .join('');

    card.innerHTML = `
      <div class="card-header">
        <h3 class="card-title">${project.title}</h3>
        <span class="card-category">${project.category}</span>
      </div>
      <p>${project.description}</p>
      <div class="tag-list">${tags}</div>
      <a class="card-link" href="${project.file}">Open project →</a>
    `;

    projectGrid.appendChild(card);
  });
}

function applyFilters() {
  const search = searchInput.value.trim().toLowerCase();
  const selectedCategory = categoryFilter.value;

  const filtered = projects.filter((project) => {
    const matchCategory = selectedCategory === 'all' || project.category === selectedCategory;
    const searchableText = [project.title, project.category, project.description, project.tags.join(' ')].join(' ').toLowerCase();
    const matchSearch = !search || searchableText.includes(search);
    return matchCategory && matchSearch;
  });

  renderProjects(filtered);
  projectCount.textContent = String(filtered.length);
}

searchInput.addEventListener('input', applyFilters);
categoryFilter.addEventListener('change', applyFilters);

projectCount.textContent = String(projects.length);
categoryCount.textContent = String(categories.length);
renderProjects(projects);
