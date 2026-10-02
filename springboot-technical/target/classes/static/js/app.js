const projectGrid = document.getElementById('project-grid');
const searchInput = document.getElementById('project-search');
const categoryFilter = document.getElementById('category-filter');
const projectCount = document.getElementById('project-count');
const categoryCount = document.getElementById('category-count');

let projects = [];

async function loadProjects() {
    try {
        const response = await fetch('/api/projects');
        if (!response.ok) {
            throw new Error(`Could not load documents (${response.status})`);
        }
        projects = await response.json();

        const categories = [...new Set(projects.map((project) => project.category))].sort();
        categoryFilter.innerHTML = '<option value="all">All categories</option>';
        categories.forEach((category) => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categoryFilter.appendChild(option);
        });

        categoryCount.textContent = String(categories.length);
        renderProjects(projects);
    } catch (error) {
        projectGrid.textContent = `Unable to load the Markdown catalog: ${error.message}`;
    }
}

function renderProjects(items) {
    projectGrid.innerHTML = '';

    if (!items.length) {
        projectGrid.innerHTML = '<article class="project-card"><p>No projects match your search.</p></article>';
        projectCount.textContent = '0';
        return;
    }

    projectCount.textContent = String(items.length);

    items.forEach((project) => {
        const card = document.createElement('article');
        card.className = 'project-card';

        const header = document.createElement('div');
        header.className = 'card-header';

        const title = document.createElement('h3');
        title.className = 'card-title';
        title.textContent = project.name;

        const category = document.createElement('span');
        category.className = 'card-category';
        category.textContent = project.category;
        header.append(title, category);

        const description = document.createElement('p');
        description.textContent = project.shortDescription;

        const tagList = document.createElement('div');
        tagList.className = 'tag-list';
        project.tags.forEach((tag) => {
            const tagElement = document.createElement('span');
            tagElement.textContent = tag;
            tagList.appendChild(tagElement);
        });

        const link = document.createElement('a');
        link.className = 'card-link';
        link.href = project.markdownFile;
        link.textContent = 'Open document →';

        card.append(header, description, tagList, link);
        projectGrid.appendChild(card);
    });
}

function applyFilters() {
    const search = searchInput.value.trim().toLowerCase();
    const selectedCategory = categoryFilter.value;

    const filtered = projects.filter((project) => {
        const matchCategory = selectedCategory === 'all' || project.category === selectedCategory;
        const searchable = [project.name, project.category, project.shortDescription, project.tags.join(' ')].join(' ').toLowerCase();
        const matchSearch = !search || searchable.includes(search);
        return matchCategory && matchSearch;
    });

    renderProjects(filtered);
}

searchInput.addEventListener('input', applyFilters);
categoryFilter.addEventListener('change', applyFilters);

loadProjects();
