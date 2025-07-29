---
description: Structure the process of building a UI component from a Figma design
---

# Figma Design Implementation Workflow

## Phase 1: Asset Extraction and Project Setup

1. **Connect to Figma Design**
   - Request Figma design URL from the user
   - Use the Framelink Figma MCP server to access the design
   - Explore available frames and components

2. **Extract Design Assets** 
   - Download all necessary icons (SVG format)
   - Save images and illustrations (JPG/PNG format)
   - Document color schemes, typography, and spacing
   - Store assets in appropriate project directories (/public/icons/, /public/images/)

## Phase 2: Project Structure Setup

3. **Define Design System**
   - Create color variables based on Figma design ensuring the color palette is the same
   - Set up typography scales and font imports
   - Configure spacing and breakpoint systems
   - Implement reusable UI utility components

## Phase 3: Component Implementation
### You MUST follow the designs from screenshots when building a component exactly, do not deviate. Only use the assets you download from the Figma designs, do not create any assets. 

4. **Build Core Layout Components**
   - Implement navigation/sidebar components
   - Create header/top bar components
   - Set up main content layout structure
   - Ensure responsive behavior matches design

5. **Implement Feature Components**

   - Build hero/banner sections
   - Create card and list components
   - Implement interactive elements (buttons, inputs)
   - Add animation and transition effects

6. **Assemble Page Layouts**
   - Combine components into full page layouts
   - Implement page-specific features
   - Ensure proper component composition
   - Verify layout matches Figma design

## Phase 4: Testing and Refinement
7. **Refinement**
   - Address any visual discrepancies
   - Optimize performance if needed
   - Ensure accessibility standards are met
   - Document any design decisions or deviations