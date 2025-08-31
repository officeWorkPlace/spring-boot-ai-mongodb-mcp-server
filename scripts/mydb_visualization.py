import matplotlib.pyplot as plt
import matplotlib
matplotlib.use('Agg')  # Use non-interactive backend
import numpy as np
import seaborn as sns
from matplotlib.patches import Rectangle
import pandas as pd

# Set the style
sns.set_style("whitegrid")
sns.set_palette("husl")

# Create figure with subplots
fig = plt.figure(figsize=(20, 15))
fig.suptitle('MyDB Database Visualization Dashboard', fontsize=20, fontweight='bold')

# 1. Database Schema Overview
ax1 = plt.subplot(3, 4, 1)
collections = ['users', 'orders', 'products', 'reviews', 'home', 'testCollection']
doc_counts = [18, 18, 18, 18, 14, 3]
colors = plt.cm.Set3(np.arange(len(collections)))

bars = ax1.bar(collections, doc_counts, color=colors)
ax1.set_title('Document Count by Collection', fontweight='bold')
ax1.set_ylabel('Number of Documents')
ax1.tick_params(axis='x', rotation=45)
for bar, count in zip(bars, doc_counts):
    ax1.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 0.5, 
             str(count), ha='center', va='bottom', fontweight='bold')

# 2. Storage Usage
ax2 = plt.subplot(3, 4, 2)
storage_sizes = [2514, 4477, 2443, 3826, 2324, 928]  # bytes
storage_kb = [size/1024 for size in storage_sizes]

wedges, texts, autotexts = ax2.pie(storage_kb, labels=collections, autopct='%1.1f%%', startangle=90)
ax2.set_title('Storage Distribution (KB)', fontweight='bold')

# 3. Average Object Size
ax3 = plt.subplot(3, 4, 3)
avg_sizes = [139, 248, 135, 212, 166, 309]
bars = ax3.bar(collections, avg_sizes, color=colors)
ax3.set_title('Average Document Size (bytes)', fontweight='bold')
ax3.set_ylabel('Bytes')
ax3.tick_params(axis='x', rotation=45)

# 4. Database Relationships Diagram
ax4 = plt.subplot(3, 4, 4)
ax4.set_xlim(0, 10)
ax4.set_ylim(0, 10)
ax4.set_title('Data Relationships', fontweight='bold')

# Draw relationship boxes
boxes = {
    'Users': (1, 8, 2, 1.5),
    'Orders': (1, 6, 2, 1.5),
    'Products': (6, 8, 2, 1.5),
    'Reviews': (6, 6, 2, 1.5)
}

for name, (x, y, w, h) in boxes.items():
    rect = Rectangle((x, y), w, h, linewidth=2, edgecolor='navy', facecolor='lightblue', alpha=0.7)
    ax4.add_patch(rect)
    ax4.text(x + w/2, y + h/2, name, ha='center', va='center', fontweight='bold')

# Draw relationships
ax4.arrow(3, 8.75, 2.8, 0, head_width=0.2, head_length=0.2, fc='red', ec='red')
ax4.text(4.5, 9.2, 'orders', ha='center', color='red', fontweight='bold')

ax4.arrow(3, 6.75, 2.8, 0, head_width=0.2, head_length=0.2, fc='green', ec='green')
ax4.text(4.5, 6.3, 'reviews', ha='center', color='green', fontweight='bold')

ax4.arrow(2, 6, 0, 1.8, head_width=0.2, head_length=0.2, fc='blue', ec='blue')
ax4.text(1.5, 7, 'user_email', ha='center', color='blue', fontweight='bold', rotation=90)

ax4.set_xticks([])
ax4.set_yticks([])

# 5. Sample Order Status Distribution
ax5 = plt.subplot(3, 4, 5)
order_statuses = ['shipped', 'pending', 'delivered', 'cancelled']
status_counts = [8, 4, 4, 2]  # Sample distribution
ax5.pie(status_counts, labels=order_statuses, autopct='%1.0f', startangle=90)
ax5.set_title('Order Status Distribution', fontweight='bold')

# 6. Product Categories
ax6 = plt.subplot(3, 4, 6)
categories = ['Electronics', 'Furniture', 'Books', 'Clothing']
category_counts = [6, 5, 4, 3]  # Sample distribution
bars = ax6.barh(categories, category_counts, color=['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'])
ax6.set_title('Products by Category', fontweight='bold')
ax6.set_xlabel('Number of Products')

# 7. User Age Distribution
ax7 = plt.subplot(3, 4, 7)
ages = np.random.normal(35, 10, 18)  # Sample age distribution
ages = np.clip(ages, 18, 65)  # Keep realistic ages
ax7.hist(ages, bins=6, color='skyblue', alpha=0.7, edgecolor='black')
ax7.set_title('User Age Distribution', fontweight='bold')
ax7.set_xlabel('Age')
ax7.set_ylabel('Number of Users')

# 8. Review Ratings
ax8 = plt.subplot(3, 4, 8)
ratings = [1, 2, 3, 4, 5]
rating_counts = [1, 2, 3, 7, 5]  # Sample rating distribution
bars = ax8.bar(ratings, rating_counts, color='gold', alpha=0.7, edgecolor='orange')
ax8.set_title('Product Review Ratings', fontweight='bold')
ax8.set_xlabel('Rating (1-5 stars)')
ax8.set_ylabel('Number of Reviews')
ax8.set_xticks(ratings)

# 9. Monthly Order Volume
ax9 = plt.subplot(3, 4, 9)
months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
order_volume = [12, 15, 18, 22, 19, 25]  # Sample monthly data
ax9.plot(months, order_volume, marker='o', linewidth=3, markersize=8, color='purple')
ax9.fill_between(months, order_volume, alpha=0.3, color='purple')
ax9.set_title('Monthly Order Trend', fontweight='bold')
ax9.set_ylabel('Number of Orders')
ax9.grid(True, alpha=0.3)

# 10. Price Range Distribution
ax10 = plt.subplot(3, 4, 10)
price_ranges = ['$0-50', '$51-200', '$201-500', '$501-1000', '$1000+']
price_counts = [3, 5, 4, 4, 2]
bars = ax10.bar(price_ranges, price_counts, color='lightcoral', alpha=0.8)
ax10.set_title('Product Price Ranges', fontweight='bold')
ax10.set_ylabel('Number of Products')
ax10.tick_params(axis='x', rotation=45)

# 11. Collection Size Comparison
ax11 = plt.subplot(3, 4, 11)
y_pos = np.arange(len(collections))
ax11.barh(y_pos, doc_counts, color=colors, alpha=0.8)
ax11.set_yticks(y_pos)
ax11.set_yticklabels(collections)
ax11.set_xlabel('Document Count')
ax11.set_title('Collection Size Comparison', fontweight='bold')

# 12. Database Health Metrics
ax12 = plt.subplot(3, 4, 12)
metrics = ['Index\nEfficiency', 'Query\nPerformance', 'Storage\nOptimization', 'Data\nIntegrity']
scores = [85, 92, 78, 95]  # Sample health scores
colors_health = ['green' if score >= 90 else 'orange' if score >= 80 else 'red' for score in scores]
bars = ax12.bar(metrics, scores, color=colors_health, alpha=0.7)
ax12.set_ylim(0, 100)
ax12.set_title('Database Health Metrics', fontweight='bold')
ax12.set_ylabel('Score (%)')

# Add score labels on bars
for bar, score in zip(bars, scores):
    ax12.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 2, 
             f'{score}%', ha='center', va='bottom', fontweight='bold')

plt.tight_layout()
plt.subplots_adjust(top=0.93)
# Save the main dashboard
plt.savefig('scripts/mydb_dashboard.png', dpi=300, bbox_inches='tight')
print("Dashboard saved as 'scripts/mydb_dashboard.png'")
plt.close()

# Additional: Create a network graph showing relationships
fig2, ax = plt.subplots(1, 1, figsize=(12, 8))
ax.set_title('MyDB Data Relationship Network', fontsize=16, fontweight='bold')

# Node positions
positions = {
    'Users': (2, 4),
    'Orders': (4, 6),
    'Products': (6, 4),
    'Reviews': (4, 2),
    'Home': (1, 1),
    'TestCollection': (7, 1)
}

# Draw nodes
for name, (x, y) in positions.items():
    circle = plt.Circle((x, y), 0.5, color='lightblue', ec='navy', linewidth=2)
    ax.add_patch(circle)
    ax.text(x, y, name, ha='center', va='center', fontweight='bold', fontsize=10)

# Draw connections
connections = [
    ('Users', 'Orders', 'user_email'),
    ('Users', 'Reviews', 'user_email'),
    ('Products', 'Orders', 'items'),
    ('Products', 'Reviews', 'product'),
]

for start, end, label in connections:
    x1, y1 = positions[start]
    x2, y2 = positions[end]
    ax.plot([x1, x2], [y1, y2], 'r-', linewidth=2, alpha=0.7)
    # Add label at midpoint
    mid_x, mid_y = (x1 + x2) / 2, (y1 + y2) / 2
    ax.text(mid_x, mid_y, label, fontsize=8, ha='center', va='center', 
            bbox=dict(boxstyle="round,pad=0.3", facecolor='white', alpha=0.8))

ax.set_xlim(0, 8)
ax.set_ylim(0, 7)
ax.set_aspect('equal')
ax.axis('off')

plt.tight_layout()
# Save the network graph
plt.savefig('scripts/mydb_network.png', dpi=300, bbox_inches='tight')
print("Network graph saved as 'scripts/mydb_network.png'")
plt.close()

print("MyDB Database Visualization Complete!")
print("\nDatabase Summary:")
print("================")
print(f"Total Collections: {len(collections)}")
print(f"Total Documents: {sum(doc_counts)}")
print(f"Total Storage: {sum(storage_sizes)/1024:.2f} KB")
print("\nKey Relationships:")
print("- Users connect to Orders via user_email")
print("- Users connect to Reviews via user_email") 
print("- Products connect to Orders via items array")
print("- Products connect to Reviews via product name")
