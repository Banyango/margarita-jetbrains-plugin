# Margarita JetBrains Plugin

[![JetBrains Marketplace](https://img.shields.io/badge/Marketplace-Margarita-blue)](https://plugins.jetbrains.com/plugin/com.margarita.margarita-jetbrains-plugin)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

Margarita language support for JetBrains IDEs (IntelliJ IDEA, PyCharm, etc.).

[Margarita](https://github.com/Banyango/margarita) is a lightweight markup language and Python library for writing, composing, and rendering structured LLM prompts. It extends Markdown with templating features like variables, conditionals, loops, and includes.

## Features

- **Syntax Highlighting**: Full color support for Margarita scripts (`.mg` files), including:
    - Markdown content (Headers, Bold, Italic, Code blocks, Lists).
    - Templating constructs (`if`, `for`).
    - Variable interpolation `{{ variable }}`.
    - Metadata blocks `{ key: value }`.
    - Includes `[[ filename.mg ]]`.
- **Navigation**: 
    - Jump to included files using `Ctrl+Click` or `Cmd+Click` on `[[ filename.mg ]]`.

## Installation

### From Disk
1. Download the latest release from the [GitHub Releases](https://github.com/Banyango/margarita-jetbrains-plugin/releases).
2. Open your IDE settings.
3. Navigate to **Plugins** -> **⚙️ (Gear Icon)** -> **Install Plugin from Disk...**.
4. Select the downloaded ZIP file.

## Usage

Create a file with the `.mg` extension. You can use standard Markdown combined with Margarita's templating features:

```margarita
{
  model: gpt-4
  temperature: 0.7
}

# Prompt Title

Hello {{ user_name }},

{% if project_context %}
Here is the context for the project:
{{ project_context }}
{% endif %}

[[ reusable_instructions.mg ]]

Please analyze the following tasks:
{% for task in tasks %}
- {{ task }}
{% endfor %}
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m 'Add some amazing feature'`).
4. Push to the branch (`git push origin feature/amazing-feature`).
5. Open a Pull Request.

## Links

- **Margarita Project**: [https://github.com/Banyango/margarita](https://github.com/Banyango/margarita)
- **Plugin Repository**: [https://github.com/Banyango/margarita-jetbrains-plugin](https://github.com/Banyango/margarita-jetbrains-plugin)