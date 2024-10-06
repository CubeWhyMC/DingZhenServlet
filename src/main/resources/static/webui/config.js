// config.js

// 配置对象，用于存储所有设置
let config = {};

// 从后端获取配置
async function fetchConfig() {
    const metaTag = document.querySelector('meta[name="profile-uuid"]');
    let profileUuid = metaTag.content;
    try {
        const response = await fetch('/api/v2/config', {
            method: 'GET',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                uuid: profileUuid
            })
        });
        config = await response.json();
        renderConfig();
    } catch (error) {
        console.error('Error fetching config:', error);
    }
}

// 渲染配置到页面
function renderConfig() {
    const container = document.querySelector('.container');
    container.innerHTML = ''; // 清空现有内容

    for (const [category, items] of Object.entries(config)) {
        const column = document.createElement('div');
        column.className = 'menu-column';
        column.innerHTML = `<h3>${category}</h3>`;

        for (const [itemName, itemConfig] of Object.entries(items)) {
            const menuItem = document.createElement('div');
            menuItem.className = 'menu-item';
            menuItem.innerHTML = `
                ${itemName}
                <div class="submenu">
                    ${renderSubmenuItems(category, itemName, itemConfig)}
                </div>
            `;
            menuItem.onclick = toggleActive;
            menuItem.oncontextmenu = toggleMenu;
            column.appendChild(menuItem);
        }

        container.appendChild(column);
    }
}

// 渲染子菜单项
function renderSubmenuItems(category, itemName, itemConfig) {
    let html = '';
    for (const [key, value] of Object.entries(itemConfig)) {
        if (typeof value === 'boolean') {
            html += `
                <div class="submenu-item">
                    <span>${key}</span>
                    <label class="toggle-switch">
                        <input type="checkbox" ${value ? 'checked' : ''} onchange="updateConfig('${category}', '${itemName}', '${key}', this.checked)">
                        <span class="slider"></span>
                    </label>
                </div>
            `;
        } else if (typeof value === 'number') {
            html += `
                <div class="submenu-item">
                    <span>${key}</span>
                    <div class="value-adjust">
                        <input type="range" min="0" max="100" value="${value}" oninput="this.nextElementSibling.value = this.value; updateConfig('${category}', '${itemName}', '${key}', Number(this.value))">
                        <input type="number" value="${value}" min="0" max="100" oninput="this.previousElementSibling.value = this.value; updateConfig('${category}', '${itemName}', '${key}', Number(this.value))">
                    </div>
                </div>
            `;
        } else if (typeof value === 'string' && value.startsWith('#')) {
            html += `
                <div class="submenu-item">
                    <span>${key}</span>
                    <input type="color" value="${value}" onchange="updateConfig('${category}', '${itemName}', '${key}', this.value)">
                </div>
            `;
        }
    }
    return html;
}

// 更新配置并发送完整配置到后端
async function updateConfig(category, itemName, key, value) {
    config[category][itemName][key] = value;
    try {
        const response = await fetch('/api/v2/config', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(config),
        });
        if (response.code !== 200) {
            throw new Error('Failed to update config');
        }
    } catch (error) {
        console.error('Error updating config:', error);
    }
}

// 切换菜单项的激活状态
function toggleActive(event) {
    event.currentTarget.classList.toggle('active');
}

// 切换子菜单的显示状态
function toggleMenu(event) {
    event.preventDefault();
    const submenu = event.currentTarget.querySelector('.submenu');
    if (submenu) {
        submenu.style.display = submenu.style.display === 'none' ? 'block' : 'none';
    }
}

// 搜索功能
function searchFunctions() {
    const searchInput = document.getElementById("search-bar").value.toLowerCase();
    const menuItems = document.querySelectorAll(".menu-item");

    menuItems.forEach(function (item) {
        const itemText = item.textContent.toLowerCase();
        if (itemText.includes(searchInput)) {
            item.style.display = "block";
        } else {
            item.style.display = "none";
        }
    });
}

// 页面加载时获取配置
document.addEventListener('DOMContentLoaded', fetchConfig);